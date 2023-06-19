package org.mixdrinks.ui

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.value.Value
import com.russhwolf.settings.Settings
import de.jensklingenberg.ktorfit.Ktorfit
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import org.mixdrinks.data.CocktailsProvider
import org.mixdrinks.data.FutureCocktailSelector
import org.mixdrinks.data.ItemsType
import org.mixdrinks.data.MixDrinksService
import org.mixdrinks.data.SnapshotRepository
import org.mixdrinks.data.TagsRepository
import org.mixdrinks.domain.CocktailSelector
import org.mixdrinks.domain.FilterPathParser
import org.mixdrinks.dto.CocktailId
import org.mixdrinks.dto.FilterId
import org.mixdrinks.ui.details.DetailsComponent
import org.mixdrinks.ui.details.FullCocktailRepository
import org.mixdrinks.ui.details.goods.GoodsRepository
import org.mixdrinks.ui.filters.main.FilterComponent
import org.mixdrinks.ui.filters.search.ItemRepository
import org.mixdrinks.ui.filters.search.SearchItemComponent
import org.mixdrinks.ui.goods.ItemDetailComponent
import org.mixdrinks.ui.goods.ItemGoodsRepository
import org.mixdrinks.ui.list.SelectedFilterProvider
import org.mixdrinks.ui.list.main.ListComponent
import org.mixdrinks.ui.list.main.MutableFilterStorage
import org.mixdrinks.ui.list.predefine.PreDefineCocktailsComponent
import org.mixdrinks.ui.list.predefine.PreDefineFilterStorage
import org.mixdrinks.ui.navigation.DeepLinkParser
import org.mixdrinks.ui.navigation.Navigator
import org.mixdrinks.ui.widgets.undomain.launch

internal object Graph {

    private val settings: Settings = Settings()

    private val json = Json {
        isLenient = true
        ignoreUnknownKeys = true
    }

    private val ktorfit = Ktorfit.Builder().httpClient(HttpClient {
        install(ContentNegotiation) {
            json(json)
        }
    }).baseUrl("https://api.mixdrinks.org/").build().create<MixDrinksService>()

    val snapshotRepository: SnapshotRepository = SnapshotRepository(ktorfit, settings, json)

    val mutableFilterStorage = MutableFilterStorage { snapshotRepository.get() }
}

internal class RootComponent(
    componentContext: ComponentContext,
) : ComponentContext by componentContext {

    private val navigation = StackNavigation<Navigator.Config>()

    private val navigator: Navigator = Navigator(navigation)

    private val _stack: Value<ChildStack<Navigator.Config, Child>> = childStack(
        source = navigation,
        initialConfiguration = Navigator.Config.ListConfig(),
        handleBackButton = true,
        childFactory = ::createChild
    )

    val stack: Value<ChildStack<Navigator.Config, Child>> = _stack

    private val deepLinkParser = DeepLinkParser(
        suspend { Graph.snapshotRepository.get() },
        FilterPathParser(),
    )

    fun onDeepLink(deepLink: String) {
        launch {
            deepLinkParser.parseDeepLink(deepLink)?.let { deepLinkAction ->
                val config: Navigator.Config = when (deepLinkAction) {
                    is DeepLinkParser.DeepLinkAction.Cocktail -> Navigator.Config.DetailsConfig(
                        deepLinkAction.id
                    )

                    is DeepLinkParser.DeepLinkAction.Filters -> {
                        Graph.mutableFilterStorage.selectMany(deepLinkAction.selectedFilters)
                        Navigator.Config.ListConfig()
                    }
                }

                coroutineScope {
                    this.launch(Dispatchers.Main) {
                        navigator.openFromDeepLink(config)
                    }
                }
            }
        }
    }

    fun onBack() {
        navigator.back()
    }

    fun buildPreDefineCocktailsComponent(
        componentContext: ComponentContext,
        itemsType: ItemsType,
    ): PreDefineCocktailsComponent {
        return PreDefineCocktailsComponent(
            componentContext = componentContext,
            cocktailsProvider = CocktailsProvider(
                snapshot = suspend { Graph.snapshotRepository.get() },
                filterRepository = PreDefineFilterStorage(
                    itemsType.type.getFilterGroup().id,
                    FilterId(itemsType.id)
                ),
                cocktailSelector = suspend {
                    CocktailSelector(Graph.mutableFilterStorage.getFilterGroups()
                        .map { it.toFilterGroup() })
                },
                tagsRepository = TagsRepository(suspend { Graph.snapshotRepository.get() })
            ),
            navigator = navigator,
        )
    }

    private fun createChild(config: Navigator.Config, componentContext: ComponentContext): Child =
        when (config) {
            is Navigator.Config.ListConfig -> Child.List(listScreen(componentContext))
            is Navigator.Config.DetailsConfig -> Child.Details(
                detailsScreen(
                    componentContext,
                    config
                )
            )

            is Navigator.Config.FilterConfig -> Child.Filters(filterScreen(componentContext))
            is Navigator.Config.SearchItemConfig -> Child.ItemSearch(
                searchItemScreen(
                    componentContext, config.searchItemType
                )
            )

            is Navigator.Config.ItemConfig -> Child.Item(
                detailGoodsScreen(
                    componentContext, config.id, config.typeGoods
                )
            )
        }

    private fun listScreen(componentContext: ComponentContext): ListComponent = ListComponent(
        componentContext = componentContext,
        cocktailsProvider = CocktailsProvider(
            suspend { Graph.snapshotRepository.get() },
            Graph.mutableFilterStorage,
            suspend {
                CocktailSelector(Graph.mutableFilterStorage.getFilterGroups()
                    .map { it.toFilterGroup() })
            },
            TagsRepository(suspend { Graph.snapshotRepository.get() }),
        ),
        selectedFilterProvider = SelectedFilterProvider(suspend { Graph.snapshotRepository.get() },
            suspend { Graph.mutableFilterStorage }),
        navigator = navigator,
        mutableFilterStorage = Graph.mutableFilterStorage,
    )

    private fun detailGoodsScreen(
        componentContext: ComponentContext, id: Int, type: String
    ): ItemDetailComponent {
        return ItemDetailComponent(
            componentContext,
            ItemGoodsRepository { Graph.snapshotRepository.get() },
            navigator,
            ItemsType(id, ItemsType.Type.fromString(type)),
            this,
        )
    }

    private fun detailsScreen(
        componentContext: ComponentContext, config: Navigator.Config.DetailsConfig
    ): DetailsComponent {
        return DetailsComponent(componentContext,
            FullCocktailRepository { Graph.snapshotRepository.get() },
            CocktailId(config.id),
            navigator,
            GoodsRepository { Graph.snapshotRepository.get() })
    }

    private fun filterScreen(componentContext: ComponentContext): FilterComponent {
        return FilterComponent(
            componentContext,
            Graph.mutableFilterStorage,
            getFutureCocktail(),
            navigator,
        )
    }

    private fun getFutureCocktail(): FutureCocktailSelector {
        return FutureCocktailSelector(
            snapshot = { Graph.snapshotRepository.get() },
            cocktailSelector = {
                CocktailSelector(Graph.mutableFilterStorage.getFilterGroups()
                    .map { it.toFilterGroup() })
            },
            mutableFilterStorage = { Graph.mutableFilterStorage },
        )
    }

    private fun searchItemScreen(
        component: ComponentContext,
        searchItemType: SearchItemComponent.SearchItemType,
    ): SearchItemComponent {
        val itemRepository = ItemRepository(
            suspend { Graph.snapshotRepository.get() }, getFutureCocktail()
        )
        return SearchItemComponent(
            component,
            searchItemType,
            Graph.mutableFilterStorage,
            itemRepository,
            navigation,
        )
    }

    sealed class Child {
        class List(val component: ListComponent) : Child()
        class Details(val component: DetailsComponent) : Child()
        class Filters(val component: FilterComponent) : Child()
        class Item(val component: ItemDetailComponent) : Child()
        class ItemSearch(val component: SearchItemComponent) : Child()
    }
}

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
import org.mixdrinks.data.MixDrinksService
import org.mixdrinks.data.SnapshotRepository
import org.mixdrinks.domain.FilterPathParser
import org.mixdrinks.dto.CocktailId
import org.mixdrinks.ui.details.DetailsComponent
import org.mixdrinks.ui.filters.main.FilterComponent
import org.mixdrinks.ui.filters.search.SearchItemComponent
import org.mixdrinks.ui.items.ItemDetailComponent
import org.mixdrinks.ui.list.main.ListComponent
import org.mixdrinks.ui.list.main.MutableFilterStorage
import org.mixdrinks.ui.navigation.DeepLinkParser
import org.mixdrinks.ui.navigation.Navigator
import org.mixdrinks.ui.tag.CommonTag
import org.mixdrinks.ui.tag.CommonTagCocktailsComponent
import org.mixdrinks.ui.widgets.undomain.launch

internal class Graph {

    private val settings: Settings = Settings()

    private val json = Json {
        isLenient = true
        ignoreUnknownKeys = true
    }

    private val ktorfit = Ktorfit.Builder()
        .httpClient(HttpClient {
            install(ContentNegotiation) {
                json(json)
            }
        })
        .baseUrl("https://api.mixdrinks.org/")
        .build()
        .create<MixDrinksService>()

    val snapshotRepository: SnapshotRepository = SnapshotRepository(ktorfit, settings, json)

    val mutableFilterStorage = MutableFilterStorage { snapshotRepository.get() }
}

internal class RootComponent(
    componentContext: ComponentContext,
    private val graph: Graph,
    private val componentsFactory: ComponentsFactory,
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
        suspend { graph.snapshotRepository.get() },
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
                        graph.mutableFilterStorage.selectMany(deepLinkAction.selectedFilters)
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

    private fun createChild(config: Navigator.Config, componentContext: ComponentContext): Child =
        when (config) {
            is Navigator.Config.ListConfig -> Child.List(
                componentsFactory.cocktailListComponent(componentContext, navigator)
            )

            is Navigator.Config.DetailsConfig -> Child.Details(
                componentsFactory.cocktailDetailsComponent(
                    componentContext,
                    CocktailId(config.id),
                    navigator
                )
            )

            is Navigator.Config.FilterConfig -> Child.Filters(
                componentsFactory.filterScreenComponent(componentContext, navigator)
            )

            is Navigator.Config.SearchItemConfig -> Child.ItemSearch(
                componentsFactory.searchItemScreen(
                    componentContext,
                    config.searchItemType,
                    navigator
                )
            )

            is Navigator.Config.ItemConfig -> Child.Item(
                componentsFactory.detailGoodsScreen(
                    componentContext, navigator, config.id, config.typeGoods
                )
            )

            is Navigator.Config.CommonTagConfig -> Child.CommonTagCocktails(
                componentsFactory.commonTagCocktailsComponent(
                    componentContext, CommonTag(config.id, config.type), navigator
                )
            )
        }

    sealed class Child {
        class List(val component: ListComponent) : Child()
        class Details(val component: DetailsComponent) : Child()
        class Filters(val component: FilterComponent) : Child()
        class Item(val component: ItemDetailComponent) : Child()
        class ItemSearch(val component: SearchItemComponent) : Child()
        class CommonTagCocktails(val component: CommonTagCocktailsComponent) : Child()
    }
}

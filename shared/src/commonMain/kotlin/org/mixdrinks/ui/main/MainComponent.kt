package org.mixdrinks.ui.main

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.value.Value
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.mixdrinks.di.ComponentsFactory
import org.mixdrinks.di.Graph
import org.mixdrinks.domain.FilterPathParser
import org.mixdrinks.dto.CocktailId
import org.mixdrinks.ui.details.DetailsComponent
import org.mixdrinks.ui.filters.main.FilterComponent
import org.mixdrinks.ui.filters.search.SearchItemComponent
import org.mixdrinks.ui.items.ItemDetailComponent
import org.mixdrinks.ui.list.main.ListComponent
import org.mixdrinks.ui.navigation.DeepLinkParser
import org.mixdrinks.ui.navigation.MainTabNavigator
import org.mixdrinks.ui.tag.CommonTag
import org.mixdrinks.ui.tag.CommonTagCocktailsComponent
import org.mixdrinks.ui.widgets.undomain.launch


internal class MainComponent(
    componentContext: ComponentContext,
    private val graph: Graph,
    private val componentsFactory: ComponentsFactory,
) : ComponentContext by componentContext {

    private val navigation = StackNavigation<MainTabNavigator.Config>()

    private val mainTabNavigator: MainTabNavigator = MainTabNavigator(navigation)

    private val _stack: Value<ChildStack<MainTabNavigator.Config, Child>> = childStack(
        source = navigation,
        initialConfiguration = MainTabNavigator.Config.ListConfig(),
        handleBackButton = true,
        childFactory = ::createChild
    )

    val stack: Value<ChildStack<MainTabNavigator.Config, Child>> = _stack

    private val deepLinkParser = DeepLinkParser(
        suspend { graph.snapshotRepository.get() },
        FilterPathParser(),
    )

    fun onDeepLink(deepLink: String) {
        launch {
            deepLinkParser.parseDeepLink(deepLink)?.let { deepLinkAction ->
                val config: MainTabNavigator.Config = when (deepLinkAction) {
                    is DeepLinkParser.DeepLinkAction.Cocktail -> MainTabNavigator.Config.DetailsConfig(
                        deepLinkAction.id
                    )

                    is DeepLinkParser.DeepLinkAction.Filters -> {
                        graph.mutableFilterStorage.selectMany(deepLinkAction.selectedFilters)
                        MainTabNavigator.Config.ListConfig()
                    }
                }

                coroutineScope {
                    this.launch(Dispatchers.Main) {
                        mainTabNavigator.openFromDeepLink(config)
                    }
                }
            }
        }
    }

    private fun createChild(config: MainTabNavigator.Config, componentContext: ComponentContext): Child =
        when (config) {
            is MainTabNavigator.Config.ListConfig -> Child.List(
                componentsFactory.cocktailListComponent(componentContext, mainTabNavigator)
            )

            is MainTabNavigator.Config.DetailsConfig -> Child.Details(
                componentsFactory.cocktailDetailsComponent(
                    componentContext,
                    CocktailId(config.id),
                    mainTabNavigator
                )
            )

            is MainTabNavigator.Config.FilterConfig -> Child.Filters(
                componentsFactory.filterScreenComponent(componentContext, mainTabNavigator)
            )

            is MainTabNavigator.Config.SearchItemConfig -> Child.ItemSearch(
                componentsFactory.searchItemScreen(
                    componentContext,
                    config.searchItemType,
                    mainTabNavigator
                )
            )

            is MainTabNavigator.Config.ItemConfig -> Child.Item(
                componentsFactory.detailGoodsScreen(
                    componentContext, mainTabNavigator, config.id, config.typeGoods
                )
            )

            is MainTabNavigator.Config.CommonTagConfig -> Child.CommonTagCocktails(
                componentsFactory.commonTagCocktailsComponent(
                    componentContext, CommonTag(config.id, config.type), mainTabNavigator
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

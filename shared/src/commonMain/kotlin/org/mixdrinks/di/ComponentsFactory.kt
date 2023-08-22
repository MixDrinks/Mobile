package org.mixdrinks.di

import com.arkivanov.decompose.ComponentContext
import org.mixdrinks.data.CocktailsProvider
import org.mixdrinks.data.FutureCocktailSelector
import org.mixdrinks.data.ItemsType
import org.mixdrinks.data.TagsRepository
import org.mixdrinks.domain.CocktailSelector
import org.mixdrinks.dto.CocktailId
import org.mixdrinks.dto.FilterId
import org.mixdrinks.ui.details.CocktailsDetailNavigation
import org.mixdrinks.ui.details.DetailsComponent
import org.mixdrinks.ui.details.FullCocktailRepository
import org.mixdrinks.ui.details.goods.GoodsRepository
import org.mixdrinks.ui.filters.main.FilterComponent
import org.mixdrinks.ui.filters.search.ItemRepository
import org.mixdrinks.ui.filters.search.SearchItemComponent
import org.mixdrinks.ui.items.ItemDetailComponent
import org.mixdrinks.ui.items.ItemDetailsNavigation
import org.mixdrinks.ui.items.ItemGoodsRepository
import org.mixdrinks.ui.list.CocktailListMapper
import org.mixdrinks.ui.list.SelectedFilterProvider
import org.mixdrinks.ui.list.main.ListComponent
import org.mixdrinks.ui.list.predefine.PreDefineCocktailsComponent
import org.mixdrinks.ui.list.predefine.PreDefineFilterStorage
import org.mixdrinks.ui.navigation.MainTabNavigator
import org.mixdrinks.ui.profile.ProfileNavigator
import org.mixdrinks.ui.profile.root.ProfileRootComponent
import org.mixdrinks.ui.profile.root.ProfileRootNavigation
import org.mixdrinks.ui.tag.CommonTag
import org.mixdrinks.ui.tag.CommonTagCocktailsComponent
import org.mixdrinks.ui.tag.CommonTagNameProvider
import org.mixdrinks.ui.tag.CommonTagNavigation
import org.mixdrinks.ui.visited.VisitedCocktailsComponent

internal class ComponentsFactory(
    private val graph: Graph,
) {

    fun cocktailListComponent(
        componentContext: ComponentContext,
        mainTabNavigator: MainTabNavigator,
    ): ListComponent =
        ListComponent(
            componentContext = componentContext,
            cocktailsProvider = CocktailsProvider(
                graph.snapshotRepository,
                graph.mutableFilterStorage,
                suspend {
                    CocktailSelector(graph.mutableFilterStorage.getFilterGroups()
                        .map { it.toFilterGroup() })
                },
                TagsRepository(graph.snapshotRepository),
            ),
            selectedFilterProvider = SelectedFilterProvider(suspend { graph.snapshotRepository.get() },
                suspend { graph.mutableFilterStorage }),
            mainTabNavigator = mainTabNavigator,
            mutableFilterStorage = graph.mutableFilterStorage,
            cocktailListMapper = CocktailListMapper(),
        )

    fun cocktailDetailsComponent(
        componentContext: ComponentContext,
        cocktailId: CocktailId,
        mainTabNavigator: CocktailsDetailNavigation,
    ): DetailsComponent {
        return DetailsComponent(
            componentContext,
            FullCocktailRepository(graph.snapshotRepository),
            cocktailId,
            mainTabNavigator,
            GoodsRepository { graph.snapshotRepository.get() },
            graph.visitedCocktailsService,
        )
    }

    fun filterScreenComponent(
        componentContext: ComponentContext,
        mainTabNavigator: MainTabNavigator,
    ): FilterComponent {
        return FilterComponent(
            componentContext,
            graph.mutableFilterStorage,
            createFutureCocktailSelector(),
            mainTabNavigator,
        )
    }

    fun searchItemScreen(
        component: ComponentContext,
        searchItemType: SearchItemComponent.SearchItemType,
        mainTabNavigator: MainTabNavigator,
    ): SearchItemComponent {
        val itemRepository =
            ItemRepository(graph.snapshotRepository, createFutureCocktailSelector())
        return SearchItemComponent(
            component,
            searchItemType,
            graph.mutableFilterStorage,
            itemRepository,
            mainTabNavigator,
        )
    }

    fun commonTagCocktailsComponent(
        component: ComponentContext,
        commonTag: CommonTag,
        navigator: CommonTagNavigation,
    ): CommonTagCocktailsComponent {
        return CommonTagCocktailsComponent(
            componentContext = component,
            commonTagNameProvider = CommonTagNameProvider(
                snapshotRepository = graph.snapshotRepository,
            ),
            cocktailsProvider = CocktailsProvider(
                snapshotRepository = graph.snapshotRepository,
                filterRepository = PreDefineFilterStorage(
                    commonTag.type.filterGroups.id,
                    FilterId(commonTag.id)
                ),
                cocktailSelector = {
                    CocktailSelector(graph.mutableFilterStorage.getFilterGroups()
                        .map { it.toFilterGroup() })
                },
                tagsRepository = TagsRepository(graph.snapshotRepository),
            ),
            commonTag = commonTag,
            profileNavigator = navigator,
            commonCocktailListMapper = CocktailListMapper(),
        )
    }

    fun detailGoodsScreen(
        componentContext: ComponentContext,
        itemDetailsNavigation: ItemDetailsNavigation,
        id: Int,
        type: String,
    ): ItemDetailComponent {
        return ItemDetailComponent(
            componentContext,
            ItemGoodsRepository(graph.snapshotRepository),
            itemDetailsNavigation,
            ItemsType(id, ItemsType.Type.valueOf(type)),
            createPredefinedCocktailsComponent(
                componentContext,
                ItemsType(id, ItemsType.Type.valueOf(type)),
                itemDetailsNavigation,
            ),
        )
    }

    fun createPredefinedCocktailsComponent(
        componentContext: ComponentContext,
        itemsType: ItemsType,
        itemDetailsNavigation: ItemDetailsNavigation,
    ): PreDefineCocktailsComponent {
        return PreDefineCocktailsComponent(
            componentContext = componentContext,
            cocktailsProvider = CocktailsProvider(
                snapshotRepository = graph.snapshotRepository,
                filterRepository = PreDefineFilterStorage(
                    itemsType.type.getFilterGroup().id,
                    FilterId(itemsType.id)
                ),
                cocktailSelector = suspend {
                    CocktailSelector(graph.mutableFilterStorage.getFilterGroups()
                        .map { it.toFilterGroup() })
                },
                tagsRepository = TagsRepository(graph.snapshotRepository)
            ),
            mainTabNavigator = itemDetailsNavigation,
            cocktailsMapper = CocktailListMapper(),
        )
    }

    private fun createFutureCocktailSelector(): FutureCocktailSelector {
        return FutureCocktailSelector(
            snapshot = { graph.snapshotRepository.get() },
            cocktailSelector = {
                CocktailSelector(graph.mutableFilterStorage.getFilterGroups()
                    .map { it.toFilterGroup() })
            },
            mutableFilterStorage = { graph.mutableFilterStorage },
        )
    }

    fun profileRootComponent(
        componentContext: ComponentContext,
        profileRootNavigation: ProfileRootNavigation,
    ): ProfileRootComponent {
        return ProfileRootComponent(
            componentContext = componentContext,
            profileRootNavigation = profileRootNavigation,
            authBus = graph.authBus,
            deleteAccountService = graph.deleteAccountService,
        )
    }

    fun visitedCocktailsComponent(
        componentContext: ComponentContext,
        profileTabNavigator: ProfileNavigator,
    ): VisitedCocktailsComponent {
        return VisitedCocktailsComponent(
            componentContext = componentContext,
            visitedCocktailsService = graph.visitedCocktailsService,
            snapshotRepository = graph.snapshotRepository,
            commonCocktailListMapper = CocktailListMapper(),
            tagsRepository = TagsRepository(graph.snapshotRepository),
            visitedCocktailsNavigation = profileTabNavigator,
        )
    }
}

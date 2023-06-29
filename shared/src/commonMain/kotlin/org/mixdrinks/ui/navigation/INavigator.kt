package org.mixdrinks.ui.navigation

import org.mixdrinks.data.ItemsType
import org.mixdrinks.dto.CocktailId
import org.mixdrinks.dto.TagId
import org.mixdrinks.dto.TasteId
import org.mixdrinks.ui.filters.search.SearchItemComponent

internal interface INavigator {

    fun back()

    fun navigateToItem(itemsType: ItemsType.Type, id: Int)

    fun navigateToDetails(cocktailId: Int)

    fun navigateToDetails(cocktailId: CocktailId)

    fun navigateToSearchItem(searchItemType: SearchItemComponent.SearchItemType)

    fun navigateToFilters()

    fun navigateToTagCocktails(tagId: TagId)

    fun navigationToTasteCocktails(tasteId: TasteId)

    fun openFromDeepLink(config: MainTabNavigator.Config)

}

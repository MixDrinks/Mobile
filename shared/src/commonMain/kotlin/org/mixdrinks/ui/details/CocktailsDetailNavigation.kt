package org.mixdrinks.ui.details

import org.mixdrinks.data.ItemsType
import org.mixdrinks.dto.CocktailId
import org.mixdrinks.dto.TagId
import org.mixdrinks.dto.TasteId

interface CocktailsDetailNavigation {

    fun navigateToItem(itemsType: ItemsType.Type, id: Int)

    fun navigateToDetails(cocktailId: CocktailId)

    fun navigateToTagCocktails(tagId: TagId)

    fun navigationToTasteCocktails(tasteId: TasteId)

    fun back()

}

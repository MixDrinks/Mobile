package org.mixdrinks.ui.items

import org.mixdrinks.dto.CocktailId
import org.mixdrinks.dto.TagId

interface ItemDetailsNavigation {

    fun back()

    fun navigateToDetails(cocktailId: CocktailId)

    fun navigateToTagCocktails(tagId: TagId)
}

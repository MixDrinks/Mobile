package org.mixdrinks.ui.visited

import org.mixdrinks.dto.CocktailId
import org.mixdrinks.dto.TagId

interface VisitedCocktailsNavigation {

    fun navigateToDetails(cocktailId: CocktailId)

    fun navigateToTagCocktails(tagId: TagId)
}

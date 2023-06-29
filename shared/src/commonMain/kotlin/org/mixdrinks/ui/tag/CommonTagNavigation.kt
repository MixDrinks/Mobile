package org.mixdrinks.ui.tag

import org.mixdrinks.dto.CocktailId
import org.mixdrinks.dto.TagId

interface CommonTagNavigation {

    fun navigateToDetails(cocktailId: CocktailId)

    fun navigateToTagCocktails(tagId: TagId)

    fun back()

}

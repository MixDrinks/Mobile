package org.mixdrinks.ui.navigation

import org.mixdrinks.dto.CocktailId

interface CocktailOpener {

    fun navigateToDetails(id: CocktailId)
}

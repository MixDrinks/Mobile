package org.mixdrinks.cocktail.ui.filters

import org.mixdrinks.domain.CocktailSelector
import org.mixdrinks.dto.CocktailId
import org.mixdrinks.dto.FilterGroupId
import org.mixdrinks.dto.FilterId

class FutureCocktailSelector(
    private val cocktailSelector: suspend () -> CocktailSelector,
    private val filterRepository: suspend () -> FilterRepository,
) {
  suspend fun getCocktailIds(futureFilterGroupId: FilterGroupId, futureFilterId: FilterId): Set<CocktailId> {
    val filters = filterRepository().getSelectedFilters().toMutableMap()
    filters[futureFilterGroupId] = filters[futureFilterGroupId].orEmpty() + futureFilterId
    return cocktailSelector().getCocktailIds(filters)
  }
}

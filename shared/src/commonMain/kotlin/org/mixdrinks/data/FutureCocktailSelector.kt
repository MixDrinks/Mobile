package org.mixdrinks.data

import org.mixdrinks.domain.CocktailSelector
import org.mixdrinks.dto.CocktailId
import org.mixdrinks.dto.FilterGroupId
import org.mixdrinks.dto.FilterId
import org.mixdrinks.dto.SnapshotDto

class FutureCocktailSelector(
    private val snapshot: suspend () -> SnapshotDto,
    private val cocktailSelector: suspend () -> CocktailSelector,
    private val filterRepository: suspend () -> FilterRepository,
) {
  suspend fun getCocktailIds(futureFilterGroupId: FilterGroupId, futureFilterId: FilterId): Set<CocktailId> {
    val filters = filterRepository().getSelectedFilters()
        .mapValues { it.value.map { it.filterId } }
        .toMutableMap()

    filters[futureFilterGroupId] = filters[futureFilterGroupId].orEmpty() + futureFilterId

    val notEmptyFilter = filters.filter { it.value.isNotEmpty() }
    return if (notEmptyFilter.isEmpty()) {
      snapshot().cocktails.map { it.id }.toSet()
    } else {
      cocktailSelector().getCocktailIds(notEmptyFilter)
    }
  }
}

package org.mixdrinks.data

import org.mixdrinks.domain.CocktailSelector
import org.mixdrinks.dto.CocktailId
import org.mixdrinks.dto.FilterGroupId
import org.mixdrinks.dto.FilterId
import org.mixdrinks.dto.SnapshotDto
import org.mixdrinks.ui.list.main.MutableFilterStorage

internal class FutureCocktailSelector(
    private val snapshot: suspend () -> SnapshotDto,
    private val cocktailSelector: suspend () -> CocktailSelector,
    private val mutableFilterStorage: suspend () -> MutableFilterStorage,
) {
    suspend fun getCocktailIds(futureFilterGroupId: FilterGroupId, futureFilterId: FilterId): Set<CocktailId> {
        val filters = mutableFilterStorage().getSelectedFilters()
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

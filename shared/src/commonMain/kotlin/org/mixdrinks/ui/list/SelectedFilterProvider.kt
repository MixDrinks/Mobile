package org.mixdrinks.ui.list

import org.mixdrinks.dto.FilterGroupId
import org.mixdrinks.dto.FilterId
import org.mixdrinks.dto.SnapshotDto
import org.mixdrinks.ui.filters.FilterItemUiModel
import org.mixdrinks.ui.list.main.MutableFilterStorage

internal class SelectedFilterProvider(
    private val snapshot: suspend () -> SnapshotDto,
    private val mutableFilterStorage: suspend () -> MutableFilterStorage,
) {

    suspend fun getSelectedFiltersWithData(): List<FilterItemUiModel> {
        return mutableFilterStorage().getSelectedFilters().flatMap { (filterGroupId, filters) ->
            filters.map { filter ->
                FilterItemUiModel(
                    groupId = filterGroupId,
                    id = filter.filterId,
                    name = getFilterName(filterGroupId, filter.filterId),
                    isSelect = true,
                    isEnable = true,
                )
            }
        }
    }

    private suspend fun getFilterName(filterGroupId: FilterGroupId, filterId: FilterId): String {
        return snapshot().filterGroups.find { it.id == filterGroupId }?.filters?.find { it.id == filterId }?.name
            ?: error("Cannot found filter")
    }
}

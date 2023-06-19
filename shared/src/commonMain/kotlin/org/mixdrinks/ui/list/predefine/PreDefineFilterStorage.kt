package org.mixdrinks.ui.list.predefine

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.mixdrinks.dto.FilterGroupId
import org.mixdrinks.dto.FilterId
import org.mixdrinks.ui.list.FilterObserver
import org.mixdrinks.ui.list.main.MutableFilterStorage

class PreDefineFilterStorage(
    filterGroupId: FilterGroupId,
    id: FilterId
) : FilterObserver {

    override val selected: StateFlow<Map<FilterGroupId, List<MutableFilterStorage.FilterSelected>>> =
        MutableStateFlow(
            mapOf(
                filterGroupId to listOf(MutableFilterStorage.FilterSelected(id, 0))
            )
        )
}

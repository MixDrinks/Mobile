package org.mixdrinks.ui.list

import kotlinx.coroutines.flow.StateFlow
import org.mixdrinks.dto.FilterGroupId
import org.mixdrinks.ui.list.main.MutableFilterStorage

interface FilterObserver {

    val selected: StateFlow<Map<FilterGroupId, List<MutableFilterStorage.FilterSelected>>>
}

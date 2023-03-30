package org.mixdrinks.data

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.mixdrinks.dto.FilterGroupDto
import org.mixdrinks.dto.FilterGroupId
import org.mixdrinks.dto.FilterId
import org.mixdrinks.dto.SelectionType
import org.mixdrinks.dto.SnapshotDto
import org.mixdrinks.ui.filters.FilterValueChangeDelegate

class FilterRepository(
    private val snapshot: suspend () -> SnapshotDto,
) : FilterValueChangeDelegate {

  private var operationCount: Long = 0

  data class FilterSelected(
      val filterId: FilterId,
      val operationIndex: Long,
  )

  private val _selected = MutableStateFlow<Map<FilterGroupId, List<FilterSelected>>>(mapOf())
  val selected: StateFlow<Map<FilterGroupId, List<FilterSelected>>> = _selected

  override fun onFilterStateChange(filterGroupId: FilterGroupId, id: FilterId, isSelect: Boolean) {
    CoroutineScope(Dispatchers.Main + SupervisorJob()).launch {
      onValueChange(filterGroupId, id, isSelect)
    }
  }

  suspend fun onValueChange(filterGroupId: FilterGroupId, id: FilterId, isSelect: Boolean) {
    val copy = _selected.value.toMutableMap()

    val selectedFilters = copy.getOrPut(filterGroupId, ::listOf).toMutableList()
    if (isSelect) {
      if (getFilterSelectionType(filterGroupId) == SelectionType.SINGLE) {
        selectedFilters.clear()
      }
      selectedFilters.add(FilterSelected(id, operationCount++))
    } else {
      selectedFilters.removeAll { it.filterId == id }
    }

    copy[filterGroupId] = selectedFilters

    _selected.emit(copy)
  }

  suspend fun clear() {
    _selected.emit(emptyMap())
  }

  suspend fun getFilterGroups(): List<FilterGroupDto> {
    return snapshot().filterGroups
  }

  fun getSelectedFilters(): Map<FilterGroupId, List<FilterSelected>> {
    return _selected.value
  }

  private suspend fun getFilterSelectionType(filterGroupId: FilterGroupId): SelectionType {
    return snapshot().filterGroups.find { it.id == filterGroupId }?.selectionType
        ?: error("Cannot found filter group")
  }
}

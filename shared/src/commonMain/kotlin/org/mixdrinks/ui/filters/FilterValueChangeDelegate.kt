package org.mixdrinks.ui.filters

import org.mixdrinks.dto.FilterGroupId
import org.mixdrinks.dto.FilterId

interface FilterValueChangeDelegate {

  fun onFilterStateChange(filterGroupId: FilterGroupId, id: FilterId, isSelect: Boolean)

  fun onFilterStateChange(filterItemUiModel: FilterItemUiModel, isSelect: Boolean) {
    onFilterStateChange(filterItemUiModel.groupId, filterItemUiModel.id, isSelect)
  }
}

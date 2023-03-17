package org.mixdrinks.cocktail.ui.filters

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.pop
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import org.mixdrinks.cocktail.ui.RootComponent
import org.mixdrinks.cocktail.ui.widgets.undomain.UiState
import org.mixdrinks.cocktail.ui.widgets.undomain.launch
import org.mixdrinks.dto.FilterGroupId
import org.mixdrinks.dto.FilterId
import kotlin.coroutines.EmptyCoroutineContext

class FilterComponent(
    private val componentContext: ComponentContext,
    private val filterRepository: FilterRepository,
    private val navigation: StackNavigation<RootComponent.Config>,
) : ComponentContext by componentContext {

  val state: StateFlow<UiState<List<FilterGroupUi>>> = flow {
    emit(filterRepository.getFilterGroups())
  }
      .combine(filterRepository.selected) { filterGroups, selected ->
        UiState.Data(filterGroups.map { filterGroupDto ->
          FilterGroupUi(
              filterGroupId = filterGroupDto.id,
              name = filterGroupDto.name,
              filterItems = filterGroupDto.filters.map { filter ->
                FilterUi(
                    id = filter.id,
                    name = filter.name,
                    isSelect = selected[filterGroupDto.id]?.contains(filter.id) ?: false
                )
              }
          )
        })
      }
      .stateIn(CoroutineScope(EmptyCoroutineContext), SharingStarted.Eagerly, UiState.Loading)

  fun close() {
    navigation.pop()
  }

  fun onValueChange(filterGroupId: FilterGroupId, id: FilterId, isSelect: Boolean) = launch {
    filterRepository.onValueChange(filterGroupId, id, isSelect)
  }

  data class FilterGroupUi(
      val filterGroupId: FilterGroupId,
      val name: String,
      val filterItems: List<FilterUi>,
  )

  data class FilterUi(
      val id: FilterId,
      val name: String,
      val isSelect: Boolean,
  )

}

package org.mixdrinks.cocktail.ui.filters

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.pop
import kotlin.coroutines.EmptyCoroutineContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.withContext
import org.mixdrinks.cocktail.ui.Graph.filterRepository
import org.mixdrinks.cocktail.ui.RootComponent
import org.mixdrinks.cocktail.ui.widgets.undomain.UiState
import org.mixdrinks.cocktail.ui.widgets.undomain.launch
import org.mixdrinks.domain.CocktailSelector
import org.mixdrinks.dto.FilterGroupDto
import org.mixdrinks.dto.FilterGroupId
import org.mixdrinks.dto.FilterId

class FilterComponent(
    private val componentContext: ComponentContext,
    private val filterRepository: FilterRepository,
    private val cocktailSelector: suspend () -> CocktailSelector,
    private val navigation: StackNavigation<RootComponent.Config>,
) : ComponentContext by componentContext {

  val state: Flow<UiState<List<FilterGroupUi>>> = filterRepository.selected
      .transform { selected ->
        this.emit(UiState.Loading)
        this.emit(
            UiState.Data(filterRepository.getFilterGroups()
                .map { filterGroupDto ->
                  FilterGroupUi(
                      filterGroupId = filterGroupDto.id,
                      name = filterGroupDto.name,
                      filterItems = buildFilterItems(filterGroupDto, selected)
                  )
                })
        )
      }
      .flowOn(Dispatchers.Default)

  private suspend fun buildFilterItems(filterGroupDto: FilterGroupDto, selected: Map<FilterGroupId, List<FilterId>>) =
      filterGroupDto.filters.map { filter ->
        val nextMap = selected.filter { it.value.isNotEmpty() }.toMutableMap().apply {
          this[filterGroupDto.id] = selected[filterGroupDto.id].orEmpty().plus(filter.id)
        }

        FilterUi(
            id = filter.id,
            name = filter.name,
            isSelect = selected[filterGroupDto.id]?.contains(filter.id) ?: false,
            isEnable = cocktailSelector().getCocktailIds(nextMap).isNotEmpty(),
        )
      }
          .sortedWith(
              compareBy(
                  { !it.isSelect },
                  { !it.isEnable }
              )
          )

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
      val isEnable: Boolean,
  )
}

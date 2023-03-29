package org.mixdrinks.cocktail.ui.filters

import androidx.compose.runtime.Immutable
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.pop
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.transform
import org.mixdrinks.cocktail.ui.RootComponent
import org.mixdrinks.cocktail.ui.widgets.undomain.UiState
import org.mixdrinks.cocktail.ui.widgets.undomain.launch
import org.mixdrinks.cocktail.ui.widgets.undomain.stateInWhileSubscribe
import org.mixdrinks.domain.CocktailSelector
import org.mixdrinks.domain.FilterGroups
import org.mixdrinks.dto.FilterGroupDto
import org.mixdrinks.dto.FilterGroupId
import org.mixdrinks.dto.FilterId
import org.mixdrinks.dto.SelectionType

class FilterComponent(
    private val componentContext: ComponentContext,
    private val filterRepository: FilterRepository,
    private val cocktailSelector: suspend () -> CocktailSelector,
    private val navigation: StackNavigation<RootComponent.Config>,
) : ComponentContext by componentContext {

  val state: StateFlow<UiState<List<FilterScreenElement>>> = filterRepository.selected
      .transform { selected ->
        this.emit(UiState.Loading)
        this.emit(
            UiState.Data(filterRepository.getFilterGroups()
                .flatMap { filterGroupDto ->
                  flatMapFilterGroup(filterGroupDto, selected)
                })
        )
      }
      .flowOn(Dispatchers.Default)
      .stateInWhileSubscribe()

  private suspend fun flatMapFilterGroup(filterGroupDto: FilterGroupDto, selected: Map<FilterGroupId, List<FilterId>>): List<FilterScreenElement> {
    val list = listOf<FilterScreenElement>(
        FilterScreenElement.Title(filterGroupDto.name),
    )

    return if (filterGroupDto.id !in listOf(FilterGroups.GOODS.id, FilterGroups.TOOLS.id)) {
      list.plus(
          FilterScreenElement.FilterGroupUi(
              filterGroupId = filterGroupDto.id,
              filterItems = buildFilterItems(filterGroupDto, selected)
          )
      )
    } else {
      list.plus(FilterScreenElement.FilterOpenSearch(filterGroupDto.id, "Відкрити ${filterGroupDto.name}"))
    }
  }

  private suspend fun buildFilterItems(
      filterGroupDto: FilterGroupDto,
      selected: Map<FilterGroupId, List<FilterId>>,
  ): List<FilterUi> {
    val filters = filterGroupDto.filters.map { filter ->
      val nextMap = selected.filter { it.value.isNotEmpty() }.toMutableMap().apply {
        this[filterGroupDto.id] = selected[filterGroupDto.id].orEmpty().plus(filter.id)
      }

      val cocktailCount = cocktailSelector().getCocktailIds(nextMap).count()

      FilterUi(
          id = filter.id,
          name = filter.name,
          isSelect = selected[filterGroupDto.id]?.contains(filter.id) ?: false,
          isEnable = filterGroupDto.selectionType == SelectionType.SINGLE || cocktailCount != 0,
          cocktailCount = cocktailCount,
      )
    }

    return when (filterGroupDto.selectionType) {
      SelectionType.SINGLE -> filters
      SelectionType.MULTIPLE -> filters.sortedWith(
          compareBy(
              { -it.cocktailCount },
              { !it.isSelect },
              { !it.isEnable }
          )
      )
    }
  }

  fun clear() = launch {
    filterRepository.clear()
  }

  fun close() {
    navigation.pop()
  }

  fun onValueChange(filterGroupId: FilterGroupId, id: FilterId, isSelect: Boolean) = launch {
    filterRepository.onValueChange(filterGroupId, id, isSelect)
  }

  fun openDetailSearch(filterGroupId: FilterGroupId) {

  }

  @Immutable
  sealed class FilterScreenElement {
    @Immutable
    data class Title(
        val name: String,
    ) : FilterScreenElement()

    @Immutable
    data class FilterGroupUi(
        val filterGroupId: FilterGroupId,
        val filterItems: List<FilterUi>,
    ) : FilterScreenElement()

    @Immutable
    data class FilterOpenSearch(
        val filterGroupId: FilterGroupId,
        val text: String,
    ) : FilterScreenElement()
  }

  @Immutable
  data class FilterUi(
      val id: FilterId,
      val name: String,
      val isSelect: Boolean,
      val isEnable: Boolean,
      val cocktailCount: Int,
  )
}

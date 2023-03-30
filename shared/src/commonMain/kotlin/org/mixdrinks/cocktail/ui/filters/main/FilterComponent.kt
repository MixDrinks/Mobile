package org.mixdrinks.cocktail.ui.filters.main

import androidx.compose.runtime.Immutable
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.push
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.transform
import org.mixdrinks.cocktail.ui.RootComponent
import org.mixdrinks.cocktail.ui.filters.FilterRepository
import org.mixdrinks.cocktail.ui.filters.FutureCocktailSelector
import org.mixdrinks.cocktail.ui.filters.search.SearchItemComponent
import org.mixdrinks.cocktail.ui.widgets.undomain.UiState
import org.mixdrinks.cocktail.ui.widgets.undomain.launch
import org.mixdrinks.cocktail.ui.widgets.undomain.stateInWhileSubscribe
import org.mixdrinks.domain.CocktailSelector
import org.mixdrinks.domain.FilterGroup
import org.mixdrinks.domain.FilterGroups
import org.mixdrinks.dto.FilterGroupDto
import org.mixdrinks.dto.FilterGroupId
import org.mixdrinks.dto.FilterId
import org.mixdrinks.dto.SelectionType

class FilterComponent(
    private val componentContext: ComponentContext,
    private val filterRepository: FilterRepository,
    private val futureCocktailSelector: FutureCocktailSelector,
    private val cocktailSelector: suspend () -> CocktailSelector,
    private val navigation: StackNavigation<RootComponent.Config>,
) : ComponentContext by componentContext {

  val state: StateFlow<UiState<List<FilterScreenElement>>> = filterRepository.selected
      .transform { selected ->
        //this.emit(UiState.Loading)
        this.emit(
            UiState.Data(filterRepository.getFilterGroups()
                .flatMap { filterGroupDto ->
                  flatMapFilterGroup(filterGroupDto, selected)
                })
        )
      }
      .flowOn(Dispatchers.Default)
      .stateInWhileSubscribe()

  private suspend fun flatMapFilterGroup(
      filterGroupDto: FilterGroupDto,
      selected: Map<FilterGroupId, List<FilterRepository.FilterSelected>>,
  ): List<FilterScreenElement> {
    val list = listOf<FilterScreenElement>(
        FilterScreenElement.Title(filterGroupDto.name),
    )

    return when (filterGroupDto.id) {
      FilterGroups.TAGS.id -> emptyList()
      !in listOf(FilterGroups.GOODS.id, FilterGroups.TOOLS.id) -> {
        list.plus(
            FilterScreenElement.FilterGroupUi(
                filterGroupId = filterGroupDto.id,
                filterItems = buildFilterItems(filterGroupDto, selected)
            )
        )
      }
      else -> {
        list
            .plus(
                FilterScreenElement.FilterGroupUi(
                    filterGroupId = filterGroupDto.id,
                    filterItems = buildSelectedFilterItems(
                        filterGroupDto,
                        selected[filterGroupDto.id].orEmpty().map { it.filterId }
                    )
                )
            )
            .plus(
                FilterScreenElement.FilterOpenSearch(
                    filterGroupDto.id,
                    "Додати ${filterGroupDto.name.lowercase()} до фільтру"
                )
            )
      }
    }
  }

  private fun buildSelectedFilterItems(filterGroupDto: FilterGroupDto, filters: List<FilterId>): List<FilterUi> {
    return filterGroupDto.filters
        .filter { it.id in filters }
        .map { filter ->
          FilterUi(
              id = filter.id,
              name = filter.name,
              isSelect = true,
              isEnable = true,
              cocktailCount = 0,
          )
        }
  }

  private suspend fun buildFilterItems(
      filterGroupDto: FilterGroupDto,
      selected: Map<FilterGroupId, List<FilterRepository.FilterSelected>>,
  ): List<FilterUi> {
    val filters = filterGroupDto.filters.map { filter ->
      val cocktailCount = futureCocktailSelector.getCocktailIds(
          filterGroupDto.id,
          filter.id,
      )
          .size

      val isSelected = selected[filterGroupDto.id].orEmpty().map { it.filterId }.contains(filter.id)
      val isEnable = isSelected || filterGroupDto.selectionType == SelectionType.SINGLE || cocktailCount != 0

      FilterUi(
          id = filter.id,
          name = filter.name,
          isSelect = isSelected,
          isEnable = isEnable,
          cocktailCount = cocktailCount,
      )
    }

    return filters
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
    val searchItemType = when (filterGroupId) {
      FilterGroups.GOODS.id -> SearchItemComponent.SearchItemType.GOODS
      FilterGroups.TOOLS.id -> SearchItemComponent.SearchItemType.TOOLS
      else -> error("Unknown filter group id: $filterGroupId")
    }

    navigation.push(RootComponent.Config.SearchItemConfig(searchItemType))
  }

  @Immutable
  sealed class FilterScreenElement(val key: Int) {
    @Immutable
    data class Title(
        val name: String,
    ) : FilterScreenElement(name.hashCode())

    @Immutable
    data class FilterGroupUi(
        val filterGroupId: FilterGroupId,
        val filterItems: List<FilterUi>,
    ) : FilterScreenElement(filterGroupId.value)

    @Immutable
    data class FilterOpenSearch(
        val filterGroupId: FilterGroupId,
        val text: String,
    ) : FilterScreenElement(-filterGroupId.value /*Use - for make key difference from Title element*/)
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

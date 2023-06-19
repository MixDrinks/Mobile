package org.mixdrinks.ui.filters.main

import androidx.compose.runtime.Immutable
import com.arkivanov.decompose.ComponentContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.transform
import org.mixdrinks.data.FutureCocktailSelector
import org.mixdrinks.domain.FilterGroups
import org.mixdrinks.dto.FilterGroupDto
import org.mixdrinks.dto.FilterGroupId
import org.mixdrinks.dto.FilterId
import org.mixdrinks.dto.SelectionType
import org.mixdrinks.ui.filters.FilterItemUiModel
import org.mixdrinks.ui.filters.FilterValueChangeDelegate
import org.mixdrinks.ui.filters.search.SearchItemComponent
import org.mixdrinks.ui.list.main.MutableFilterStorage
import org.mixdrinks.ui.navigation.Navigator
import org.mixdrinks.ui.widgets.undomain.UiState
import org.mixdrinks.ui.widgets.undomain.launch
import org.mixdrinks.ui.widgets.undomain.stateInWhileSubscribe

internal class FilterComponent(
    private val componentContext: ComponentContext,
    private val mutableFilterStorage: MutableFilterStorage,
    private val futureCocktailSelector: FutureCocktailSelector,
    private val navigator: Navigator,
) : ComponentContext by componentContext,
    FilterValueChangeDelegate by mutableFilterStorage {

    val state: StateFlow<UiState<List<FilterScreenElement>>> = mutableFilterStorage.selected
        .transform { selected ->
            this.emit(
                UiState.Data(mutableFilterStorage.getFilterGroups()
                    .flatMap { filterGroupDto ->
                        flatMapFilterGroup(filterGroupDto, selected)
                    })
            )
        }
        .flowOn(Dispatchers.Default)
        .stateInWhileSubscribe()

    private suspend fun flatMapFilterGroup(
        filterGroupDto: FilterGroupDto,
        selected: Map<FilterGroupId, List<MutableFilterStorage.FilterSelected>>,
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

    private fun buildSelectedFilterItems(
        filterGroupDto: FilterGroupDto,
        filters: List<FilterId>,
    ): List<FilterItemUiModel> {
        return filterGroupDto.filters
            .filter { it.id in filters }
            .map { filter ->
                FilterItemUiModel(
                    groupId = filterGroupDto.id,
                    id = filter.id,
                    name = filter.name,
                    isSelect = true,
                    isEnable = true,
                )
            }
    }

    private suspend fun buildFilterItems(
        filterGroupDto: FilterGroupDto,
        selected: Map<FilterGroupId, List<MutableFilterStorage.FilterSelected>>,
    ): List<FilterItemUiModel> {
        val filters = filterGroupDto.filters.map { filter ->
            val cocktailCount = futureCocktailSelector.getCocktailIds(
                filterGroupDto.id,
                filter.id,
            )
                .size

            val isSelected =
                selected[filterGroupDto.id].orEmpty().map { it.filterId }.contains(filter.id)
            val isEnable =
                isSelected || filterGroupDto.selectionType == SelectionType.SINGLE || cocktailCount != 0

            FilterItemUiModel(
                groupId = filterGroupDto.id,
                id = filter.id,
                name = filter.name,
                isSelect = isSelected,
                isEnable = isEnable,
            )
        }

        return filters
    }

    fun clear() = launch {
        mutableFilterStorage.clear()
    }

    fun close() {
        navigator.back()
    }

    fun openDetailSearch(filterGroupId: FilterGroupId) {
        val searchItemType = when (filterGroupId) {
            FilterGroups.GOODS.id -> SearchItemComponent.SearchItemType.GOODS
            FilterGroups.TOOLS.id -> SearchItemComponent.SearchItemType.TOOLS
            else -> error("Unknown filter group id: $filterGroupId")
        }

        navigator.navigateToSearchItem(searchItemType)
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
            val filterItems: List<FilterItemUiModel>,
        ) : FilterScreenElement(filterGroupId.value)

        @Immutable
        data class FilterOpenSearch(
            val filterGroupId: FilterGroupId,
            val text: String,
        ) : FilterScreenElement(-filterGroupId.value /*Use - for make key difference from Title element*/)
    }

}

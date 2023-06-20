package org.mixdrinks.ui.filters.search

import androidx.compose.runtime.Immutable
import com.arkivanov.decompose.ComponentContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.transform
import org.mixdrinks.domain.FilterGroups
import org.mixdrinks.domain.ImageUrlCreators
import org.mixdrinks.dto.FilterGroupId
import org.mixdrinks.dto.FilterId
import org.mixdrinks.ui.list.main.MutableFilterStorage
import org.mixdrinks.ui.navigation.Navigator
import org.mixdrinks.ui.widgets.undomain.UiState
import org.mixdrinks.ui.widgets.undomain.launch
import org.mixdrinks.ui.widgets.undomain.stateInWhileSubscribe

internal class SearchItemComponent(
    private val componentContext: ComponentContext,
    private val searchItemType: SearchItemType,
    private val mutableFilterStorage: MutableFilterStorage,
    private val itemRepository: ItemRepository,
    private val navigator: Navigator,
) : ComponentContext by componentContext {

    private val _textState = MutableStateFlow("")
    val textState: StateFlow<String> = _textState

    val state: StateFlow<UiState<List<ItemUiModel>>> = mutableFilterStorage.selected
        .map {
            it[searchItemType.filterGroupId] ?: emptyList()
        }
        .transform { selected ->
            this.emit(
                UiState.Data(
                    mapItemsToUi(
                        itemRepository.getItems(searchItemType),
                        selected,
                    )
                )
            )
        }
        .combine(textState) { items, query ->
            items.copy(
                data = items.data.filter { it.name.contains(query, ignoreCase = true) }
            )
        }
        .flowOn(Dispatchers.Default)
        .stateInWhileSubscribe()

    private fun mapItemsToUi(
        items: List<ItemRepository.ItemDto>,
        selected: List<MutableFilterStorage.FilterSelected>,
    ): List<ItemUiModel> {
        return items
            .map { item ->
                val imageUrl = when (item.id) {
                    is ItemRepository.ItemId.Good -> ImageUrlCreators
                        .createUrl(item.id.id, ImageUrlCreators.Size.SIZE_320)

                    is ItemRepository.ItemId.Tool -> ImageUrlCreators
                        .createUrl(item.id.id, ImageUrlCreators.Size.SIZE_320)
                }

                val inSelected = selected.find { it.filterId == FilterId(item.id.value) }

                val isSelect = inSelected != null

                val operationIndex = inSelected?.operationIndex ?: -1L

                ItemUiModel(
                    id = item.id,
                    name = item.name,
                    imageUrl = imageUrl,
                    isSelected = isSelect,
                    count = item.cocktailCount,
                    operationIndex = operationIndex,
                )
            }
            .sortedWith(
                compareByDescending<ItemUiModel> { it.isSelected }
                    .then { a, b ->
                        if (a.isSelected) {
                            compareBy<ItemUiModel> { it.operationIndex }.compare(a, b)
                        } else {
                            compareBy<ItemUiModel>({ -it.count }, { it.name }).compare(a, b)
                        }
                    }
            )
    }

    fun close() {
        navigator.back()
    }

    fun onSearchQueryChanged(query: String) = launch {
        _textState.emit(query)
    }

    fun onItemClicked(id: ItemRepository.ItemId, isSelected: Boolean) = launch {
        mutableFilterStorage.onValueChange(
            searchItemType.filterGroupId,
            FilterId(id.value),
            isSelected
        )
    }

    @Immutable
    data class ItemUiModel(
        val id: ItemRepository.ItemId,
        val name: String,
        val imageUrl: String,
        val isSelected: Boolean,
        val count: Int,
        val operationIndex: Long,
    )

    enum class SearchItemType(val filterGroupId: FilterGroupId) {
        GOODS(FilterGroups.GOODS.id), TOOLS(FilterGroups.TOOLS.id)
    }
}

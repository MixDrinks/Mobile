package org.mixdrinks.ui.filters.search

import androidx.compose.runtime.Immutable
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.pop
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.transform
import org.mixdrinks.data.FilterRepository
import org.mixdrinks.domain.FilterGroups
import org.mixdrinks.domain.ImageUrlCreators
import org.mixdrinks.dto.FilterGroupId
import org.mixdrinks.dto.FilterId
import org.mixdrinks.ui.RootComponent
import org.mixdrinks.ui.widgets.undomain.UiState
import org.mixdrinks.ui.widgets.undomain.launch
import org.mixdrinks.ui.widgets.undomain.stateInWhileSubscribe

internal class SearchItemComponent(
    private val componentContext: ComponentContext,
    private val searchItemType: SearchItemType,
    private val filterRepository: FilterRepository,
    private val itemRepository: ItemRepository,
    private val navigation: StackNavigation<RootComponent.Config>,
) : ComponentContext by componentContext {

  private val _textState = MutableStateFlow("")
  val textState: StateFlow<String> = _textState

  val state: StateFlow<UiState<List<ItemUiModel>>> = filterRepository.selected
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
      selected: List<FilterRepository.FilterSelected>,
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
    navigation.pop()
  }

  fun onSearchQueryChanged(query: String) = launch {
    _textState.emit(query)
  }

  fun onItemClicked(id: ItemRepository.ItemId, isSelected: Boolean) = launch {
    filterRepository.onValueChange(searchItemType.filterGroupId, FilterId(id.value), isSelected)
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

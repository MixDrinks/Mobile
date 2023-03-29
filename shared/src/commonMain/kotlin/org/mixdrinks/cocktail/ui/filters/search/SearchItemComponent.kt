package org.mixdrinks.cocktail.ui.filters.search

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.parcelable.Parcelize
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.transform
import org.mixdrinks.cocktail.ui.filters.FilterRepository
import org.mixdrinks.cocktail.ui.filters.main.FilterComponent
import org.mixdrinks.cocktail.ui.widgets.undomain.UiState
import org.mixdrinks.cocktail.ui.widgets.undomain.stateInWhileSubscribe
import org.mixdrinks.domain.FilterGroups
import org.mixdrinks.domain.ImageUrlCreators
import org.mixdrinks.dto.FilterGroupId
import org.mixdrinks.dto.FilterId

class SearchItemComponent(
    private val componentContext: ComponentContext,
    private val searchItemType: SearchItemType,
    private val filterRepository: FilterRepository,
    private val itemRepository: ItemRepository,
) : ComponentContext by componentContext {

  val state: StateFlow<UiState<List<ItemUiModel>>> = filterRepository.selected
      .map {
        it[searchItemType.filterGroupId] ?: emptyList()
      }
      .transform { selected ->
        this.emit(UiState.Loading)
        this.emit(
            UiState.Data(mapItemsToUi(itemRepository.getItems(searchItemType), selected))
        )
      }
      .flowOn(Dispatchers.Default)
      .stateInWhileSubscribe()

  private fun mapItemsToUi(items: List<ItemRepository.ItemDto>, selected: List<FilterId>): List<ItemUiModel> {
    return items.map { item ->
      val imageUrl = when (item.id) {
        is ItemRepository.ItemId.Good -> ImageUrlCreators.createUrl(item.id.id, ImageUrlCreators.Size.SIZE_320)
        is ItemRepository.ItemId.Tool -> ImageUrlCreators.createUrl(item.id.id, ImageUrlCreators.Size.SIZE_320)
      }
      ItemUiModel(
          id = item.id,
          name = item.name,
          imageUrl = imageUrl,
          isSelected = item.id.value in selected.map { it.value }
      )
    }
  }

  data class ItemUiModel(
      val id: ItemRepository.ItemId,
      val name: String,
      val imageUrl: String,
      val isSelected: Boolean,
  )

  enum class SearchItemType(val filterGroupId: FilterGroupId) {
    GOODS(FilterGroups.GOODS.id), TOOLS(FilterGroups.TOOLS.id)
  }
}

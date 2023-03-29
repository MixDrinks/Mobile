package org.mixdrinks.cocktail.ui.filters.search

import org.mixdrinks.dto.GoodId
import org.mixdrinks.dto.SnapshotDto
import org.mixdrinks.dto.ToolId

class ItemRepository(
    private val snapshot: suspend () -> SnapshotDto,
) {

  suspend fun getItems(searchItemType: SearchItemComponent.SearchItemType): List<ItemDto> {
    return when (searchItemType) {
      SearchItemComponent.SearchItemType.GOODS -> getGoods()
      SearchItemComponent.SearchItemType.TOOLS -> getTools()
    }
  }

  private suspend fun getTools(): List<ItemDto> {
    return snapshot().tools
        .map {
          ItemDto(
              id = ItemId.Tool(it.id),
              name = it.name,
          )
        }
  }

  private suspend fun getGoods(): List<ItemDto> {
    return snapshot().goods
        .map {
          ItemDto(
              id = ItemId.Good(it.id),
              name = it.name,
          )
        }
  }

  sealed class ItemId(val value: Int) {
    data class Good(val id: GoodId) : ItemId(id.id)
    data class Tool(val id: ToolId) : ItemId(id.id)
  }

  data class ItemDto(
      val id: ItemId,
      val name: String,
  )
}

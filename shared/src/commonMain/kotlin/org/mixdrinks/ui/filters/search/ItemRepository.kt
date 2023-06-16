package org.mixdrinks.ui.filters.search

import org.mixdrinks.data.FutureCocktailSelector
import org.mixdrinks.domain.FilterGroups
import org.mixdrinks.dto.FilterId
import org.mixdrinks.dto.GoodId
import org.mixdrinks.dto.SnapshotDto
import org.mixdrinks.dto.ToolId

internal class ItemRepository(
    private val snapshot: suspend () -> SnapshotDto,
    private val futureCocktailSelector: FutureCocktailSelector,
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
                    cocktailCount = futureCocktailSelector.getCocktailIds(
                        FilterGroups.TOOLS.id,
                        FilterId(it.id.id),
                    ).size,
                )
            }
    }

    private suspend fun getGoods(): List<ItemDto> {
        return snapshot().goods
            .map {
                ItemDto(
                    id = ItemId.Good(it.id),
                    name = it.name,
                    cocktailCount = futureCocktailSelector.getCocktailIds(
                        FilterGroups.GOODS.id,
                        FilterId(it.id.id),
                    ).size,
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
        val cocktailCount: Int,
    )
}

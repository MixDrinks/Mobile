package org.mixdrinks.ui.items

import org.mixdrinks.data.DetailItemsUiModel
import org.mixdrinks.domain.ImageUrlCreators
import org.mixdrinks.dto.GlasswareId
import org.mixdrinks.dto.GoodId
import org.mixdrinks.dto.SnapshotDto
import org.mixdrinks.dto.ToolId


internal class ItemsRepository(
    private val snapshot: suspend () -> SnapshotDto,
) {
    suspend fun getDetailItem(goodId: GoodId): DetailItemsUiModel {
        val good = snapshot().goods.find { it.id.id == goodId.id }
            ?: error("Goods ${goodId.id} not found")
        return DetailItemsUiModel(
            good.id.id, good.name, good.about,
            ImageUrlCreators.createUrl(
                good.id,
                ImageUrlCreators.Size.SIZE_320
            )
        )
    }

    suspend fun getDetailItem(toolId: ToolId): DetailItemsUiModel {
        val tool = snapshot().tools.find { it.id.id == toolId.id }
            ?: error("Tool ${toolId.id} not found")
        return DetailItemsUiModel(
            tool.id.id, tool.name, tool.about,
            ImageUrlCreators.createUrl(
                tool.id,
                ImageUrlCreators.Size.SIZE_320
            )
        )
    }

    suspend fun getDetailItem(glasswareId: GlasswareId): DetailItemsUiModel {
        val glassware = snapshot().glassware.find { it.id == glasswareId }
            ?: error("Glassware $glasswareId not found")
        return DetailItemsUiModel(
            glassware.id.value, glassware.name, glassware.about,
            ImageUrlCreators.createUrl(
                glassware.id,
                ImageUrlCreators.Size.SIZE_320
            )
        )
    }
}


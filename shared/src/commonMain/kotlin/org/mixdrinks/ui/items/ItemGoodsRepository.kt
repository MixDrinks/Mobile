package org.mixdrinks.ui.items

import org.mixdrinks.data.DetailGoodsUiModel
import org.mixdrinks.data.SnapshotRepository
import org.mixdrinks.domain.ImageUrlCreators
import org.mixdrinks.dto.GlasswareId
import org.mixdrinks.dto.GoodId
import org.mixdrinks.dto.ToolId


internal class ItemGoodsRepository(
    private val snapshot: SnapshotRepository,
) {
    suspend fun getGoodDetails(goodId: GoodId): DetailGoodsUiModel {
        val good = snapshot.get().goods.find { it.id.id == goodId.id }
            ?: error("Goods ${goodId.id} not found")
        return DetailGoodsUiModel(
            good.id.id, good.name, good.about,
            ImageUrlCreators.createUrl(
                good.id,
                ImageUrlCreators.Size.SIZE_320
            )
        )
    }

    suspend fun getToolDetails(toolId: ToolId): DetailGoodsUiModel {
        val tool = snapshot.get().tools.find { it.id.id == toolId.id }
            ?: error("Tool ${toolId.id} not found")
        return DetailGoodsUiModel(
            tool.id.id, tool.name, tool.about,
            ImageUrlCreators.createUrl(
                tool.id,
                ImageUrlCreators.Size.SIZE_320
            )
        )
    }

    suspend fun getGlasswareDetails(glasswareId: GlasswareId): DetailGoodsUiModel {
        val glassware = snapshot.get().glassware.find { it.id == glasswareId }
            ?: error("Glassware $glasswareId not found")
        return DetailGoodsUiModel(
            glassware.id.value, glassware.name, glassware.about,
            ImageUrlCreators.createUrl(
                glassware.id,
                ImageUrlCreators.Size.SIZE_320
            )
        )
    }
}


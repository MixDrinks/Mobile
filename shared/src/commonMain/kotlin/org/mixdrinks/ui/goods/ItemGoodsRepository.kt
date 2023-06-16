package org.mixdrinks.ui.goods

import org.mixdrinks.data.DetailGoodsUiModel
import org.mixdrinks.domain.ImageUrlCreators
import org.mixdrinks.dto.GlasswareId
import org.mixdrinks.dto.GoodId
import org.mixdrinks.dto.SnapshotDto
import org.mixdrinks.dto.ToolId


internal class ItemGoodsRepository(
    private val snapshot: suspend () -> SnapshotDto,
) {
    suspend fun getDetailGood(goodId: GoodId): DetailGoodsUiModel {
        val good = snapshot().goods.find { it.id.id == goodId.id }
            ?: error("Goods ${goodId.id} not found");
        return DetailGoodsUiModel(
            good.id.id, good.name, good.about,
            ImageUrlCreators.createUrl(
                good.id,
                ImageUrlCreators.Size.SIZE_320
            )
        )
    }

    suspend fun getDetailGood(toolId: ToolId): DetailGoodsUiModel {
        val tool = snapshot().tools.find { it.id.id == toolId.id }
            ?: error("Tool ${toolId.id} not found");
        return DetailGoodsUiModel(
            tool.id.id, tool.name, tool.about,
            ImageUrlCreators.createUrl(
                tool.id,
                ImageUrlCreators.Size.SIZE_320
            )
        )
    }

    suspend fun getDetailGood(glasswareId: GlasswareId): DetailGoodsUiModel {
        val glassware = snapshot().glassware.find { it.id == glasswareId }
            ?: error("Glassware $glasswareId not found");
        return DetailGoodsUiModel(
            glassware.id.value, glassware.name, glassware.about,
            ImageUrlCreators.createUrl(
                glassware.id,
                ImageUrlCreators.Size.SIZE_320
            )
        )
    }
}


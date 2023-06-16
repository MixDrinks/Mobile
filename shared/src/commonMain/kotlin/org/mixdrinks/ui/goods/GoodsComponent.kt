package org.mixdrinks.ui.goods

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.pop
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import org.mixdrinks.data.DetailGoodsUiModel
import org.mixdrinks.data.GoodsType
import org.mixdrinks.dto.GlasswareId
import org.mixdrinks.dto.GoodId
import org.mixdrinks.dto.ToolId
import org.mixdrinks.ui.RootComponent
import org.mixdrinks.ui.widgets.undomain.UiState
import org.mixdrinks.ui.widgets.undomain.stateInWhileSubscribe

internal class GoodsComponent(
    private val componentContext: ComponentContext,
    private val goodsRepository: ItemGoodsRepository,
    private val navigation: StackNavigation<RootComponent.Config>,
    private val goodsType: GoodsType
) : ComponentContext by componentContext {

    val state: StateFlow<UiState<DetailGoodsUiModel>> = when (goodsType.type) {
        GoodsType.Type.GOODS -> flow {
            emit(goodsRepository.getDetailGood(GoodId(goodsType.id)))
        }

        GoodsType.Type.TOOL -> flow {
            emit(goodsRepository.getDetailGood(ToolId(goodsType.id)))
        }

        GoodsType.Type.GLASSWARE -> flow {
            emit(goodsRepository.getDetailGood(GlasswareId(goodsType.id)))
        }
    }
        .map { good: DetailGoodsUiModel ->
            UiState.Data(good)
        }
        .flowOn(Dispatchers.Default)
        .stateInWhileSubscribe()


    fun close() {
        navigation.pop()
    }
}


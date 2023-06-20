package org.mixdrinks.ui.items

import com.arkivanov.decompose.ComponentContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import org.mixdrinks.data.DetailGoodsUiModel
import org.mixdrinks.data.ItemsType
import org.mixdrinks.dto.GlasswareId
import org.mixdrinks.dto.GoodId
import org.mixdrinks.dto.ToolId
import org.mixdrinks.ui.RootComponent
import org.mixdrinks.ui.list.predefine.PreDefineCocktailsComponent
import org.mixdrinks.ui.navigation.Navigator
import org.mixdrinks.ui.widgets.undomain.UiState
import org.mixdrinks.ui.widgets.undomain.stateInWhileSubscribe

internal class ItemDetailComponent(
    private val componentContext: ComponentContext,
    private val goodsRepository: ItemGoodsRepository,
    private val navigator: Navigator,
    private val itemsType: ItemsType,
    public val predefineCocktailComponent: PreDefineCocktailsComponent,
) : ComponentContext by componentContext {

    val state: StateFlow<UiState<DetailGoodsUiModel>> = when (itemsType.type) {
        ItemsType.Type.GOODS -> flow {
            emit(goodsRepository.getGoodDetails(GoodId(itemsType.id)))
        }

        ItemsType.Type.TOOL -> flow {
            emit(goodsRepository.getToolDetails(ToolId(itemsType.id)))
        }

        ItemsType.Type.GLASSWARE -> flow {
            emit(goodsRepository.getGlasswareDetails(GlasswareId(itemsType.id)))
        }
    }
        .map { good: DetailGoodsUiModel ->
            UiState.Data(good)
        }
        .flowOn(Dispatchers.Default)
        .stateInWhileSubscribe()

    fun close() {
        navigator.back()
    }
}

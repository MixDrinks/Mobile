package org.mixdrinks.ui.items

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.pop
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import org.mixdrinks.data.DetailItemsUiModel
import org.mixdrinks.data.ItemsType
import org.mixdrinks.dto.GlasswareId
import org.mixdrinks.dto.GoodId
import org.mixdrinks.dto.ToolId
import org.mixdrinks.ui.RootComponent
import org.mixdrinks.ui.widgets.undomain.UiState
import org.mixdrinks.ui.widgets.undomain.stateInWhileSubscribe

internal class ItemsComponent(
    private val componentContext: ComponentContext,
    private val itemsRepository: ItemsRepository,
    private val navigation: StackNavigation<RootComponent.Config>,
    private val itemsType: ItemsType
) : ComponentContext by componentContext {

    val state: StateFlow<UiState<DetailItemsUiModel>> = when (itemsType.type) {
        ItemsType.Type.GOODS -> flow {
            emit(itemsRepository.getDetailItem(GoodId(itemsType.id)))
        }

        ItemsType.Type.TOOL -> flow {
            emit(itemsRepository.getDetailItem(ToolId(itemsType.id)))
        }

        ItemsType.Type.GLASSWARE -> flow {
            emit(itemsRepository.getDetailItem(GlasswareId(itemsType.id)))
        }
    }
        .map { item: DetailItemsUiModel ->
            UiState.Data(item)
        }
        .flowOn(Dispatchers.Default)
        .stateInWhileSubscribe()


    fun close() {
        navigation.pop()
    }
}


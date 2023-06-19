package org.mixdrinks.ui.details

import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.intl.Locale
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.push
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import org.mixdrinks.data.FullCocktail
import org.mixdrinks.data.ItemsType
import org.mixdrinks.domain.ImageUrlCreators
import org.mixdrinks.dto.CocktailId
import org.mixdrinks.dto.GlasswareId
import org.mixdrinks.dto.GoodId
import org.mixdrinks.dto.TagId
import org.mixdrinks.dto.TasteId
import org.mixdrinks.dto.ToolId
import org.mixdrinks.ui.RootComponent
import org.mixdrinks.ui.details.goods.GoodsRepository
import org.mixdrinks.ui.details.goods.GoodsSubComponent
import org.mixdrinks.ui.navigation.Navigator
import org.mixdrinks.ui.widgets.undomain.UiState
import org.mixdrinks.ui.widgets.undomain.stateInWhileSubscribe

internal class DetailsComponent(
    private val componentContext: ComponentContext,
    private val fullCocktailRepository: FullCocktailRepository,
    private val cocktailId: CocktailId,
    private val navigator: Navigator,
    goodsRepository: GoodsRepository,
) : ComponentContext by componentContext {

    val goodsSubComponent = GoodsSubComponent(
        componentContext,
        goodsRepository,
        cocktailId
    )

    val state: StateFlow<UiState<FullCocktailUiModel>> = flow {
        fullCocktailRepository.getFullCocktail(cocktailId)?.let {
            emit(it)
        }
    }
        .map { cocktail: FullCocktail ->
            UiState.Data(map(cocktail))
        }
        .flowOn(Dispatchers.Default)
        .stateInWhileSubscribe()

    private fun map(fullCocktail: FullCocktail): FullCocktailUiModel {
        return FullCocktailUiModel(
            id = fullCocktail.id,
            name = fullCocktail.name,
            url = ImageUrlCreators.createUrl(fullCocktail.id, ImageUrlCreators.Size.SIZE_560),
            receipt = fullCocktail.receipt,
            glassware = FullCocktailUiModel.GlasswareUi(
                id = fullCocktail.glassware.id,
                name = fullCocktail.glassware.name,
                url = ImageUrlCreators.createUrl(
                    fullCocktail.glassware.id,
                    ImageUrlCreators.Size.SIZE_400
                )
            ),
            tools = fullCocktail.tools.map {
                FullCocktailUiModel.ToolUi(
                    id = it.toolId,
                    name = it.name,
                    url = ImageUrlCreators.createUrl(it.toolId, ImageUrlCreators.Size.SIZE_400)
                )
            },
            tags = fullCocktail.tastes.map {
                FullCocktailUiModel.TagUi.Taste(
                    id = it.id,
                    name = it.name.capitalize(Locale.current),
                )
            }.plus(fullCocktail.tags.map {
                FullCocktailUiModel.TagUi.Tag(
                    id = it.id,
                    name = it.name.capitalize(Locale.current),
                )
            })
        )
    }

    fun close() {
        navigator.back()
    }

    @Suppress("EmptyFunctionBlock", "UnusedPrivateMember")
    fun onTagClick(tagId: TagId) {
    }

    @Suppress("EmptyFunctionBlock", "UnusedPrivateMember")
    fun onTasteClick(tasteId: TasteId) {

    }

    fun onGoodClick(goodId: GoodId) {
        navigator.navigateToItem(ItemsType.Type.GOODS, goodId.id)
    }

    fun onGlasswareClick(glasswareId: GlasswareId) {
        navigator.navigateToItem(ItemsType.Type.GLASSWARE, glasswareId.value)
    }

    fun onToolClick(toolId: ToolId) {
        navigator.navigateToItem(ItemsType.Type.TOOL, toolId.id)
    }
}

internal data class FullCocktailUiModel(
    val id: CocktailId,
    val url: String,
    val name: String,
    val receipt: List<String>,
    val tools: List<ToolUi>,
    val glassware: GlasswareUi,
    val tags: List<TagUi>,
) {

    data class ToolUi(
        val id: ToolId,
        val name: String,
        val url: String,
    )

    data class GlasswareUi(
        val id: GlasswareId,
        val name: String,
        val url: String,
    )

    sealed class TagUi(open val name: String) {
        data class Tag(
            val id: TagId,
            override val name: String,
        ) : TagUi(name)

        data class Taste(
            val id: TasteId,
            override val name: String,
        ) : TagUi(name)
    }
}

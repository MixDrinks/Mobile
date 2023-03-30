package org.mixdrinks.cocktail.ui.details

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.pop
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import org.mixdrinks.cocktail.data.FullCocktail
import org.mixdrinks.cocktail.ui.RootComponent
import org.mixdrinks.cocktail.ui.details.goods.GoodsRepository
import org.mixdrinks.cocktail.ui.details.goods.GoodsSubComponent
import org.mixdrinks.cocktail.ui.widgets.undomain.UiState
import org.mixdrinks.cocktail.ui.widgets.undomain.stateInWhileSubscribe
import org.mixdrinks.domain.ImageUrlCreators
import org.mixdrinks.dto.CocktailId
import org.mixdrinks.dto.GlasswareId
import org.mixdrinks.dto.TagId
import org.mixdrinks.dto.TasteId
import org.mixdrinks.dto.ToolId

class DetailsComponent(
    private val componentContext: ComponentContext,
    private val fullCocktailRepository: FullCocktailRepository,
    private val cocktailId: CocktailId,
    private val navigation: StackNavigation<RootComponent.Config>,
    private val goodsRepository: GoodsRepository,
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
        tools = fullCocktail.tools.map {
          FullCocktailUiModel.ToolUi.Tool(
              id = it.toolId,
              name = it.name,
              url = ImageUrlCreators.createUrl(it.toolId, ImageUrlCreators.Size.SIZE_400)
          )
        }.plus(FullCocktailUiModel.ToolUi.Glassware(
            id = fullCocktail.glassware.id,
            name = fullCocktail.glassware.name,
            url = ImageUrlCreators.createUrl(fullCocktail.glassware.id, ImageUrlCreators.Size.SIZE_400)
        )),
        tags = fullCocktail.tags.map {
          FullCocktailUiModel.TagUi.Tag(
              id = it.id,
              name = it.name,
          )
        }.plus(fullCocktail.tastes.map {
          FullCocktailUiModel.TagUi.Taste(
              id = it.id,
              name = it.name,
          )
        })
    )
  }

  fun close() {
    navigation.pop()
  }

  @Suppress("EmptyFunctionBlock", "UnusedPrivateMember")
  fun onTagClick(tagId: TagId) {

  }

  @Suppress("EmptyFunctionBlock", "UnusedPrivateMember")
  fun onTasteClick(tasteId: TasteId) {

  }
}

data class FullCocktailUiModel(
    val id: CocktailId,
    val url: String,
    val name: String,
    val receipt: List<String>,
    val tools: List<ToolUi>,
    val tags: List<TagUi>,
) {

  sealed class ToolUi(
      open val name: String,
      open val url: String,
  ) {
    data class Tool(
        val id: ToolId,
        override val name: String,
        override val url: String,
    ) : ToolUi(name, url)

    data class Glassware(
        val id: GlasswareId,
        override val name: String,
        override val url: String,
    ) : ToolUi(name, url)
  }


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

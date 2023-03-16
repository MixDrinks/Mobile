package org.mixdrinks.cocktail.ui.details

import com.arkivanov.decompose.ComponentContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import org.mixdrinks.cocktail.data.CocktailsRepository
import org.mixdrinks.cocktail.data.FullCocktail
import org.mixdrinks.dto.CocktailId
import org.mixdrinks.dto.GlasswareId
import org.mixdrinks.dto.GoodId
import org.mixdrinks.dto.TagId
import org.mixdrinks.dto.TasteId
import org.mixdrinks.dto.ToolId
import org.mixdrinks.utils.ImageUrlCreators

class DetailsComponent(
    private val componentContext: ComponentContext,
    private val cocktailsRepository: CocktailsRepository,
    private val cocktailId: CocktailId,
    public val close: () -> Unit,
) : ComponentContext by componentContext {

  private val _counter = MutableStateFlow(1)

  val state: StateFlow<UiState> = flow {
    cocktailsRepository.getFullCocktail(cocktailId)?.let {
      emit(it)
    }
  }
      .combine(_counter) { cocktail: FullCocktail, count: Int ->
        UiState.Data(map(cocktail, count))
      }
      .stateIn(CoroutineScope(Dispatchers.Default), SharingStarted.Eagerly, UiState.Loading)

  private fun map(fullCocktail: FullCocktail, count: Int): FullCocktailUiModel {
    return FullCocktailUiModel(
        id = fullCocktail.id,
        name = fullCocktail.name,
        url = ImageUrlCreators.createUrl(fullCocktail.id, ImageUrlCreators.Size.SIZE_560),
        receipt = fullCocktail.receipt,
        goods = FullCocktailUiModel.GoodsUi(
            count = count,
            goods = fullCocktail.goods.map {
              FullCocktailUiModel.GoodUi(
                  goodId = it.goodId,
                  name = it.name,
                  amount = "${it.amount * count} ${it.unit}",
                  url = ImageUrlCreators.createUrl(it.goodId, ImageUrlCreators.Size.SIZE_400)
              )
            }
        ),
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

  fun onTagClick(tagId: TagId) {

  }

  fun onTasteClick(tasteId: TasteId) {

  }

  fun onPlusClick() {
    _counter.value++
  }

  fun onMinusClick() {
    if (_counter.value > 1) {
      _counter.value--
    }
  }

  sealed class UiState {
    object Loading : UiState()
    data class Data(val data: FullCocktailUiModel) : UiState()

    object Error : UiState()
  }
}

data class FullCocktailUiModel(
    val id: CocktailId,
    val url: String,
    val name: String,
    val receipt: List<String>,
    val goods: GoodsUi,
    val tools: List<ToolUi>,
    val tags: List<TagUi>,
) {

  data class GoodsUi(
      val count: Int,
      val goods: List<GoodUi>,
  )

  data class GoodUi(
      val goodId: GoodId,
      val url: String,
      val name: String,
      val amount: String,
  )

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

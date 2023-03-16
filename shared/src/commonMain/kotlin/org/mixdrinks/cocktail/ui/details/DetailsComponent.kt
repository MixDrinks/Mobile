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
        url = "https://image.mixdrinks.org/cocktails/${fullCocktail.id.id}/560/${fullCocktail.id.id}.webp",
        receipt = fullCocktail.receipt,
        goods = FullCocktailUiModel.GoodsUi(
            count = count,
            goods = fullCocktail.goods.map {
              FullCocktailUiModel.GoodUi(
                  goodId = it.goodId,
                  name = it.name,
                  amount = "${it.amount * count} ${it.unit}",
                  url = "https://image.mixdrinks.org/goods/${it.goodId.id}/400/${it.goodId.id}.webp"
              )
            }
        ),
        tools = fullCocktail.tools.map {
          FullCocktailUiModel.ToolUi(
              id = it.toolId,
              name = it.name,
              url = "https://image.mixdrinks.org/goods/${it.toolId.id}/400/${it.toolId.id}.webp"
          )
        },
        tags = fullCocktail.tags.map {
          FullCocktailUiModel.TagUi(
              id = it.id,
              name = it.name,
          )
        },
        taste = fullCocktail.tastes.map {
          FullCocktailUiModel.TasteUi(
              id = it.id,
              name = it.name,
          )
        },
        glassware = FullCocktailUiModel.GlasswareUi(
            id = fullCocktail.glassware.id,
            name = fullCocktail.glassware.name,
            url = "https://image.mixdrinks.org/goods/${fullCocktail.glassware.id.value}/400/${fullCocktail.glassware.id.value}.webp"
        )
    )
  }

  fun onTagClick(tagId: TagId) {

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
    val taste: List<TasteUi>,
    val glassware: GlasswareUi,
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

  data class ToolUi(
      val id: ToolId,
      val name: String,
      val url: String,
  )

  data class TagUi(
      val id: TagId,
      val name: String,
  )

  data class TasteUi(
      val id: TasteId,
      val name: String,
  )

  data class GlasswareUi(
      val id: GlasswareId,
      val name: String,
      val url: String,
  )
}

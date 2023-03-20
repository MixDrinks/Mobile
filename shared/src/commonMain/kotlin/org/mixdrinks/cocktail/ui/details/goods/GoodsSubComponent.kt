package org.mixdrinks.cocktail.ui.details.goods

import com.arkivanov.decompose.ComponentContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import org.mixdrinks.cocktail.ui.widgets.undomain.UiState
import org.mixdrinks.cocktail.ui.widgets.undomain.stateInWhileSubscribe
import org.mixdrinks.dto.CocktailId
import org.mixdrinks.dto.GoodId
import org.mixdrinks.utils.ImageUrlCreators

class GoodsSubComponent(
    private val componentContext: ComponentContext,
    private val goodsRepository: GoodsRepository,
    private val cocktailId: CocktailId,
) : ComponentContext by componentContext {

  private val _counter = MutableStateFlow(1)

  val state: StateFlow<UiState<GoodsUi>> = flow {
    emit(goodsRepository.getGoods(cocktailId))
  }
      .combine(_counter) { goods: List<GoodsRepository.Good>, count: Int ->
        UiState.Data(
            GoodsUi(
                count = count,
                goods = goods.map { good ->
                  GoodUi(
                      goodId = good.goodId,
                      url = ImageUrlCreators.createUrl(good.goodId, ImageUrlCreators.Size.SIZE_320),
                      name = good.name,
                      amount = "${good.amount * count} ${good.unit}"
                  )
                }
            )
        )
      }
      .flowOn(Dispatchers.Default)
      .stateInWhileSubscribe()

  fun onPlusClick() {
    _counter.value++
  }

  fun onMinusClick() {
    if (_counter.value > 1) {
      _counter.value--
    }
  }

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
}

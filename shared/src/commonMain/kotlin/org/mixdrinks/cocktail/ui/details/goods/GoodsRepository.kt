package org.mixdrinks.cocktail.ui.details.goods

import org.mixdrinks.dto.CocktailId
import org.mixdrinks.dto.GoodId
import org.mixdrinks.dto.SnapshotDto

class GoodsRepository(
    private val snapshot: suspend () -> SnapshotDto,
) {

  suspend fun getGoods(cocktailId: CocktailId): List<Good> {
    val cocktail = snapshot().cocktails.find { it.id == cocktailId }
        ?: error("Cocktail $cocktailId not found")
    val goods = cocktail.goods.map {
      Good(
          goodId = it.goodId,
          name = snapshot().goods.first { good -> good.id == it.goodId }.name,
          amount = it.amount,
          unit = it.unit,
      )
    }

    return goods;
  }

  data class Good(
      val goodId: GoodId,
      val name: String,
      val amount: Int,
      val unit: String,
  )
}

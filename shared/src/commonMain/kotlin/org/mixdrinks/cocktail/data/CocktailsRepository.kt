package org.mixdrinks.cocktail.data

import org.mixdrinks.dto.CocktailId
import org.mixdrinks.utils.LazySuspend


class CocktailsRepository(private val mixDrinksService: MixDrinksService) {

  private val snapshot = LazySuspend { mixDrinksService.getSnapshot() }

  suspend fun getCocktails() = snapshot().cocktails

  suspend fun getFullCocktail(cocktailId: CocktailId): FullCocktail? {
    val cocktail = snapshot().cocktails.find { it.id == cocktailId } ?: return null
    val goods = cocktail.goods.map {
      FullCocktail.Good(
          goodId = it.goodId,
          name = snapshot().goods.first { good -> good.id == it.goodId }.name,
          amount = it.amount,
          unit = it.unit,
      )
    }
    val tools = snapshot().tools.filter { cocktail.tools.contains(it.id) }
        .map {
          FullCocktail.Tool(
              toolId = it.id,
              name = it.name,
          )
        }
    val tastes = snapshot().tastes.filter { cocktail.tastes.contains(it.id) }
        .map {
          FullCocktail.Taste(
              id = it.id,
              name = it.name,
          )
        }
    val tags = snapshot().tags.filter { cocktail.tags.contains(it.id) }
        .map {
          FullCocktail.Tag(
              id = it.id,
              name = it.name,
          )
        }

    val glassware = snapshot().glassware.find { it.id == cocktail.glassware } ?: return null

    return FullCocktail(
        id = cocktail.id,
        name = cocktail.name,
        receipt = cocktail.receipt,
        goods = goods,
        tools = tools,
        tags = tags,
        tastes = tastes,
        glassware = FullCocktail.Glassware(id = glassware.id, name = glassware.name),
    )
  }
}

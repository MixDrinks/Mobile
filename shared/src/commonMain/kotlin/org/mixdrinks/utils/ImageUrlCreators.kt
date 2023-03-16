package org.mixdrinks.utils

import org.mixdrinks.dto.CocktailId
import org.mixdrinks.dto.GlasswareId
import org.mixdrinks.dto.GoodId
import org.mixdrinks.dto.ToolId

object ImageUrlCreators {

  enum class Size(
      val path: String,
  ) {
    SIZE_320("320"), SIZE_400("400"), SIZE_560("560")
  }

  fun createUrl(cocktailId: CocktailId, size: Size): String {
    return "https://image.mixdrinks.org/cocktails/${cocktailId.id}/${size.path}/${cocktailId.id}.webp"
  }

  fun createUrl(goodId: GoodId, size: Size): String {
    return createToolUrl(goodId.id, size)
  }

  fun createUrl(toolId: ToolId, size: Size): String {
    return createToolUrl(toolId.id, size)
  }

  fun createUrl(glasswareId: GlasswareId, size: Size): String {
    return createToolUrl(glasswareId.value, size)
  }

  private fun createToolUrl(id: Int, size: Size): String {
    return "https://image.mixdrinks.org/goods/${id}/${size.path}/${id}.webp"
  }

}
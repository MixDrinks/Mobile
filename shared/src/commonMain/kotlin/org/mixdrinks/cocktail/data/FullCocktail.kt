package org.mixdrinks.cocktail.data

import org.mixdrinks.dto.CocktailId
import org.mixdrinks.dto.GlasswareId
import org.mixdrinks.dto.TagId
import org.mixdrinks.dto.TasteId
import org.mixdrinks.dto.ToolId

data class FullCocktail(
    val id: CocktailId,
    val name: String,
    val receipt: List<String>,
    val tools: List<Tool>,
    val tags: List<Tag>,
    val tastes: List<Taste>,
    val glassware: Glassware,
) {
  data class Tool(
      val toolId: ToolId,
      val name: String,
  )

  data class Tag(
      val id: TagId,
      val name: String,
  )

  data class Taste(
      val id: TasteId,
      val name: String,
  )

  data class Glassware(
      val id: GlasswareId,
      val name: String,
  )
}

package org.mixdrinks.ui.visited

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.mixdrinks.dto.CocktailId

@Serializable
data class VisitedCocktail(
    @SerialName("id")
    val id: CocktailId,
)

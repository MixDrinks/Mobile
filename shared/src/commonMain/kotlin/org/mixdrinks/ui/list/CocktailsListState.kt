package org.mixdrinks.ui.list

import androidx.compose.runtime.Immutable
import org.mixdrinks.dto.CocktailId
import org.mixdrinks.dto.TagId
import org.mixdrinks.ui.filters.FilterItemUiModel

@Immutable
internal sealed class CocktailsListState {
    @Immutable
    data class Cocktails(
        val list: List<Cocktail>,
    ) : CocktailsListState() {
        @Immutable
        data class Cocktail(
            val id: CocktailId,
            val url: String,
            val name: String,
            val tags: List<TagUIModel>,
        )
    }

    @Immutable
    data class TagUIModel(
        val id: TagId,
        val name: String,
    )

    @Immutable
    data class PlaceHolder(
        val filters: List<FilterItemUiModel>,
    ) : CocktailsListState()
}

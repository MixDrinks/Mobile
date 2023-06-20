package org.mixdrinks.ui.list

import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.intl.Locale
import org.mixdrinks.data.CocktailsProvider
import org.mixdrinks.domain.ImageUrlCreators

internal class CocktailListMapper {

    fun map(cocktails: List<CocktailsProvider.Cocktail>): List<CocktailsListState.Cocktails.Cocktail> {
        return cocktails.map { cocktail ->
            CocktailsListState.Cocktails.Cocktail(
                id = cocktail.id,
                url = ImageUrlCreators.createUrl(
                    cocktail.id, ImageUrlCreators.Size.SIZE_400
                ),
                name = cocktail.name,
                tags = cocktail.tags.map {
                    CocktailsListState.TagUIModel(
                        it.id, it.name.capitalize(Locale.current)
                    )
                },
            )
        }
    }
}

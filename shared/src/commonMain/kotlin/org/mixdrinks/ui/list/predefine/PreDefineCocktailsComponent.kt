package org.mixdrinks.ui.list.predefine

import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.intl.Locale
import com.arkivanov.decompose.ComponentContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import org.mixdrinks.data.CocktailsProvider
import org.mixdrinks.domain.ImageUrlCreators
import org.mixdrinks.dto.CocktailId
import org.mixdrinks.ui.list.CocktailsListState
import org.mixdrinks.ui.navigation.Navigator
import org.mixdrinks.ui.widgets.undomain.UiState
import org.mixdrinks.ui.widgets.undomain.stateInWhileSubscribe

internal class PreDefineCocktailsComponent(
    private val componentContext: ComponentContext,
    private val cocktailsProvider: CocktailsProvider,
    private val navigator: Navigator,
) : ComponentContext by componentContext {

    val state: StateFlow<UiState<CocktailsListState>> = flow {
        emitAll(cocktailsProvider.getCocktails().map { cocktails ->
            map(cocktails)
        })
    }
        .flowOn(Dispatchers.Default)
        .stateInWhileSubscribe()

    private fun map(cocktails: List<CocktailsProvider.Cocktail>): UiState.Data<CocktailsListState.Cocktails> {
        return UiState.Data(
            CocktailsListState.Cocktails(cocktails.map { cocktail ->
                CocktailsListState.Cocktails.Cocktail(
                    id = cocktail.id,
                    url = ImageUrlCreators.createUrl(
                        cocktail.id, ImageUrlCreators.Size.SIZE_400
                    ),
                    name = cocktail.name,
                    tags = cocktail.tags.map { it.capitalize(Locale.current) },
                )
            })
        )
    }

    fun onCocktailClick(cocktailId: CocktailId) {
        navigator.navigateToDetails(cocktailId.id)
    }
}

package org.mixdrinks.ui.list.predefine

import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.intl.Locale
import com.arkivanov.decompose.ComponentContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import org.mixdrinks.data.CocktailsProvider
import org.mixdrinks.domain.ImageUrlCreators
import org.mixdrinks.dto.CocktailId
import org.mixdrinks.ui.list.CocktailsListState
import org.mixdrinks.ui.navigation.Navigator

internal class PreDefineCocktailsComponent(
    private val componentContext: ComponentContext,
    private val cocktailsProvider: CocktailsProvider,
    private val navigator: Navigator,
) : ComponentContext by componentContext {

    val state: StateFlow<CocktailsListState.Cocktails> = flow {
        emitAll(cocktailsProvider.getCocktails().map { cocktails ->
            map(cocktails)
        })
    }
        .flowOn(Dispatchers.Default)
        .distinctUntilChanged()
        .stateIn(
            CoroutineScope(Dispatchers.Main),
            SharingStarted.WhileSubscribed(),
            CocktailsListState.Cocktails(emptyList())
        )

    private fun map(cocktails: List<CocktailsProvider.Cocktail>): CocktailsListState.Cocktails {
        return CocktailsListState.Cocktails(cocktails.map { cocktail ->
            CocktailsListState.Cocktails.Cocktail(
                id = cocktail.id,
                url = ImageUrlCreators.createUrl(
                    cocktail.id, ImageUrlCreators.Size.SIZE_400
                ),
                name = cocktail.name,
                tags = cocktail.tags.map { it.capitalize(Locale.current) },
            )
        }
        )
    }

    fun onCocktailClick(cocktailId: CocktailId) {
        navigator.navigateToDetails(cocktailId.id)
    }
}

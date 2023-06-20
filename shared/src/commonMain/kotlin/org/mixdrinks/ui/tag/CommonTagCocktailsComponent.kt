package org.mixdrinks.ui.tag

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
import org.mixdrinks.ui.list.CocktailsListState
import org.mixdrinks.ui.navigation.CocktailOpener
import org.mixdrinks.ui.widgets.undomain.scope

internal class CommonTagCocktailsComponent(
    private val componentContext: ComponentContext,
    private val commonTagNameProvider: CommonTagNameProvider,
    private val cocktailsProvider: CocktailsProvider,
    private val commonTag: CommonTag,
    private val cocktailOpener: CocktailOpener,
) : ComponentContext by componentContext,
    CocktailOpener by cocktailOpener {

    val name: StateFlow<String> = flow {
        emit(commonTagNameProvider.getName(commonTag) ?: "")
    }
        .map {
            "Коктейлі $it"
        }
        .stateIn(
            scope = componentContext.scope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = ""
        )

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
        })
    }
}

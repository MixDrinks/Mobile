package org.mixdrinks.ui.tag

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
import org.mixdrinks.ui.list.CocktailListMapper
import org.mixdrinks.ui.list.CocktailsListState
import org.mixdrinks.ui.widgets.undomain.scope

internal class CommonTagCocktailsComponent(
    private val componentContext: ComponentContext,
    private val commonTagNameProvider: CommonTagNameProvider,
    private val cocktailsProvider: CocktailsProvider,
    private val commonTag: CommonTag,
    private val profileNavigator: CommonTagNavigation,
    private val commonCocktailListMapper: CocktailListMapper,
) : ComponentContext by componentContext,
    CommonTagNavigation by profileNavigator {

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
            CocktailsListState.Cocktails(commonCocktailListMapper.map(cocktails))
        })
    }
        .flowOn(Dispatchers.Default)
        .distinctUntilChanged()
        .stateIn(
            CoroutineScope(Dispatchers.Main),
            SharingStarted.WhileSubscribed(),
            CocktailsListState.Cocktails(emptyList())
        )
}

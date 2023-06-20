package org.mixdrinks.ui.list.predefine

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
import org.mixdrinks.dto.CocktailId
import org.mixdrinks.ui.list.CocktailListMapper
import org.mixdrinks.ui.list.CocktailsListState
import org.mixdrinks.ui.navigation.INavigator
import org.mixdrinks.ui.navigation.Navigator

internal class PreDefineCocktailsComponent(
    private val componentContext: ComponentContext,
    private val cocktailsProvider: CocktailsProvider,
    private val navigator: Navigator,
    private val cocktailsMapper: CocktailListMapper,
) : ComponentContext by componentContext, INavigator by navigator {

    val state: StateFlow<CocktailsListState.Cocktails> = flow {
        emitAll(cocktailsProvider.getCocktails().map { cocktails ->
            CocktailsListState.Cocktails(cocktailsMapper.map(cocktails))
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

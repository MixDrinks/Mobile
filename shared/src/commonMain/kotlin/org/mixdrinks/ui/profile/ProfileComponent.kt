package org.mixdrinks.ui.profile

import com.arkivanov.decompose.ComponentContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import org.mixdrinks.ui.visited.UserVisitedCocktailsService
import org.mixdrinks.ui.widgets.undomain.UiState
import org.mixdrinks.ui.widgets.undomain.stateInWhileSubscribe

internal class ProfileComponent(
    private val componentContext: ComponentContext,
    private val visitedCocktailsService: UserVisitedCocktailsService,
) : ComponentContext by componentContext {

    val state: StateFlow<UiState<String>> = flow {
        emit(UiState.Loading)

        val cocktailIds = visitedCocktailsService.getVisitedCocktails()

        emit(UiState.Data("Hello World ${cocktailIds.map { it.id }}}"))
    }
        .flowOn(Dispatchers.Default)
        .stateInWhileSubscribe()

}

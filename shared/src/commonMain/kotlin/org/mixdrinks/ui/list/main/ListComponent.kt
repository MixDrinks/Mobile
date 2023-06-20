package org.mixdrinks.ui.list.main

import com.arkivanov.decompose.ComponentContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import org.mixdrinks.data.CocktailsProvider
import org.mixdrinks.ui.filters.FilterItemUiModel
import org.mixdrinks.ui.list.CocktailListMapper
import org.mixdrinks.ui.list.CocktailsListState
import org.mixdrinks.ui.list.SelectedFilterProvider
import org.mixdrinks.ui.navigation.INavigator
import org.mixdrinks.ui.navigation.Navigator
import org.mixdrinks.ui.widgets.undomain.UiState
import org.mixdrinks.ui.widgets.undomain.stateInWhileSubscribe

internal class ListComponent(
    private val componentContext: ComponentContext,
    private val cocktailsProvider: CocktailsProvider,
    private val selectedFilterProvider: SelectedFilterProvider,
    private val navigator: Navigator,
    private val mutableFilterStorage: MutableFilterStorage,
    private val cocktailListMapper: CocktailListMapper,
) : ComponentContext by componentContext,
    INavigator by navigator {

    val state: StateFlow<UiState<CocktailsListState>> = flow {
        emitAll(cocktailsProvider.getCocktails().map { cocktails ->
            map(cocktails)
        })
    }
        .flowOn(Dispatchers.Default)
        .stateInWhileSubscribe()

    val filterCountState: StateFlow<Int?> = mutableFilterStorage.selected
        .map { filterGroups ->
            filterGroups.flatMap { it.value }.count().takeIf { it > 0 }
        }
        .stateIn(
            CoroutineScope(Dispatchers.Main), SharingStarted.WhileSubscribed(), null
        )

    private suspend fun map(cocktails: List<CocktailsProvider.Cocktail>): UiState.Data<CocktailsListState> {
        return UiState.Data(
            if (cocktails.isEmpty()) {
                CocktailsListState.PlaceHolder(selectedFilterProvider.getSelectedFiltersWithData())
            } else {
                CocktailsListState.Cocktails(cocktailListMapper.map(cocktails))
            }
        )
    }

    fun onFilterStateChange(
        filterGroupId: FilterItemUiModel,
        isSelect: Boolean
    ) {
        mutableFilterStorage.onFilterStateChange(filterGroupId, isSelect)
    }
}

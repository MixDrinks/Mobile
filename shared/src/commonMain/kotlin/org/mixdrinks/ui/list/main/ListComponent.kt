package org.mixdrinks.ui.list.main

import com.arkivanov.decompose.ComponentContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combineTransform
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import org.mixdrinks.data.CocktailsProvider
import org.mixdrinks.ui.filters.FilterItemUiModel
import org.mixdrinks.ui.list.CocktailListMapper
import org.mixdrinks.ui.list.CocktailsListState
import org.mixdrinks.ui.list.SelectedFilterProvider
import org.mixdrinks.ui.navigation.INavigator
import org.mixdrinks.ui.navigation.MainTabNavigator
import org.mixdrinks.ui.widgets.undomain.UiState
import org.mixdrinks.ui.widgets.undomain.launch

@Suppress("LongParameterList")
internal class ListComponent(
    private val componentContext: ComponentContext,
    private val cocktailsProvider: CocktailsProvider,
    private val selectedFilterProvider: SelectedFilterProvider,
    private val mainTabNavigator: MainTabNavigator,
    private val mutableFilterStorage: MutableFilterStorage,
    private val cocktailListMapper: CocktailListMapper,
) : ComponentContext by componentContext,
    INavigator by mainTabNavigator {

    private val _state = MutableStateFlow<UiState<CocktailsListState>>(UiState.Loading)
    val state: StateFlow<UiState<CocktailsListState>> = _state

    private val _searchQuery: MutableStateFlow<String> = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _isSearchActive = MutableStateFlow(false)
    val isSearchActive: StateFlow<Boolean> = _isSearchActive

    init {
        launch {
            cocktailsProvider.getCocktails()
                .map { cocktails -> map(cocktails) }
                .combineTransform(isSearchActive) { cocktails, isSearchActive ->
                    if (!isSearchActive) {
                        emit(cocktails)
                    }
                }
                .collect(_state)
        }
        launch {
            searchQuery
                .map { query ->
                    cocktailsProvider.getAllCocktails()
                        .filter { cocktail ->
                            cocktail.name.contains(query, ignoreCase = true)
                        }
                        .sortedBy { it.name }
                }
                .map { cocktails -> map(cocktails) }
                .combineTransform(isSearchActive) { cocktails, isSearchActive ->
                    if (isSearchActive) {
                        emit(cocktails)
                    }
                }
                .collect(_state)
        }
    }

    val filterCountState: StateFlow<Int?> = mutableFilterStorage.selected
        .map { filterGroups ->
            filterGroups.flatMap { it.value }.count().takeIf { it > 0 }
        }
        .stateIn(
            CoroutineScope(Dispatchers.Main), SharingStarted.WhileSubscribed(), null
        )

    fun openSearch() = launch {
        _isSearchActive.emit(true)
    }

    fun closeSearch() = launch {
        _searchQuery.emit("")
        _isSearchActive.emit(false)
    }

    fun onSearchQueryChange(query: String) = launch {
        println("onSearchQueryChange: $query")
        _searchQuery.emit(query)
    }

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
        isSelect: Boolean,
    ) {
        mutableFilterStorage.onFilterStateChange(filterGroupId, isSelect)
    }
}

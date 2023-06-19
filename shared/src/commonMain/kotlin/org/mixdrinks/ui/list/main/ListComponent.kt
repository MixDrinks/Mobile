package org.mixdrinks.ui.list.main

import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.intl.Locale
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
import org.mixdrinks.domain.ImageUrlCreators
import org.mixdrinks.dto.CocktailId
import org.mixdrinks.ui.filters.FilterItemUiModel
import org.mixdrinks.ui.list.CocktailsListState
import org.mixdrinks.ui.list.SelectedFilterProvider
import org.mixdrinks.ui.navigation.Navigator
import org.mixdrinks.ui.widgets.undomain.UiState
import org.mixdrinks.ui.widgets.undomain.stateInWhileSubscribe

internal class ListComponent(
    private val componentContext: ComponentContext,
    private val cocktailsProvider: CocktailsProvider,
    private val selectedFilterProvider: SelectedFilterProvider,
    private val navigator: Navigator,
    private val mutableFilterStorage: MutableFilterStorage,
) : ComponentContext by componentContext {

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
            }
        )
    }

    fun onFilterStateChange(
        filterGroupId: FilterItemUiModel,
        isSelect: Boolean
    ) {
        mutableFilterStorage.onFilterStateChange(filterGroupId, isSelect)
    }

    fun openFilters() {
        navigator.navigateToFilters()
    }

    fun onCocktailClick(cocktailId: CocktailId) {
        navigator.navigateToDetails(cocktailId.id)
    }
}

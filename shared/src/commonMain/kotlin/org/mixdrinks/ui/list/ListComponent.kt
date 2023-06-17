package org.mixdrinks.ui.list

import androidx.compose.runtime.Immutable
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.intl.Locale
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.push
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import org.mixdrinks.data.CocktailListRepository
import org.mixdrinks.data.TagsRepository
import org.mixdrinks.domain.ImageUrlCreators
import org.mixdrinks.dto.CocktailDto
import org.mixdrinks.dto.CocktailId
import org.mixdrinks.ui.Graph.filterRepository
import org.mixdrinks.ui.RootComponent
import org.mixdrinks.ui.filters.FilterItemUiModel
import org.mixdrinks.ui.filters.FilterValueChangeDelegate
import org.mixdrinks.ui.widgets.undomain.UiState
import org.mixdrinks.ui.widgets.undomain.stateInWhileSubscribe

internal class ListComponent(
    private val componentContext: ComponentContext,
    private val cocktailListRepository: CocktailListRepository,
    private val selectedFilterProvider: SelectedFilterProvider,
    private val tagsRepository: TagsRepository,
    private val navigation: StackNavigation<RootComponent.Config>,
) : ComponentContext by componentContext,
    FilterValueChangeDelegate by filterRepository {

    val state: StateFlow<UiState<CocktailsListState>> = flow {
        emitAll(cocktailListRepository.getCocktails()
            .map { cocktails ->
                map(cocktails)
            })
    }
        .flowOn(Dispatchers.Default)
        .stateInWhileSubscribe()

    private suspend fun map(cocktails: List<CocktailDto>): UiState.Data<CocktailsListState> {
        return UiState.Data(
            if (cocktails.isEmpty()) {
                CocktailsListState.PlaceHolder(
                    selectedFilterProvider.getSelectedFiltersWithData()
                )
            } else {
                CocktailsListState.Cocktails(
                    cocktails.map { cocktail ->
                        CocktailsListState.Cocktails.Cocktail(
                            id = cocktail.id,
                            url = ImageUrlCreators.createUrl(
                                cocktail.id,
                                ImageUrlCreators.Size.SIZE_400
                            ),
                            name = cocktail.name,
                            tags = tagsRepository.getTags(cocktail.tags).map {
                                it.name.capitalize(Locale.current)
                            },
                        )
                    }
                )
            }
        )
    }

    fun openFilters() {
        navigation.push(RootComponent.Config.FilterConfig)
    }

    fun onCocktailClick(cocktailId: CocktailId) {
        navigation.push(RootComponent.Config.DetailsConfig(id = cocktailId.id))
    }

    @Immutable
    sealed class CocktailsListState {
        @Immutable
        data class Cocktails(
            val list: List<Cocktail>,
        ) : CocktailsListState() {
            @Immutable
            data class Cocktail(
                val id: CocktailId,
                val url: String,
                val name: String,
                val tags: List<String>,
            )
        }

        @Immutable
        data class PlaceHolder(
            val filters: List<FilterItemUiModel>,
        ) : CocktailsListState()
    }
}

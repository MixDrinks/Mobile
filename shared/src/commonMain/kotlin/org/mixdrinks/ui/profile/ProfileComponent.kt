package org.mixdrinks.ui.profile

import com.arkivanov.decompose.ComponentContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import org.mixdrinks.data.CocktailsProvider
import org.mixdrinks.data.SnapshotRepository
import org.mixdrinks.data.TagsRepository
import org.mixdrinks.domain.ImageUrlCreators
import org.mixdrinks.dto.CocktailId
import org.mixdrinks.ui.list.CocktailListMapper
import org.mixdrinks.ui.list.CocktailsListState
import org.mixdrinks.ui.visited.UserVisitedCocktailsService
import org.mixdrinks.ui.widgets.undomain.UiState
import org.mixdrinks.ui.widgets.undomain.stateInWhileSubscribe

internal class ProfileComponent(
    private val componentContext: ComponentContext,
    private val visitedCocktailsService: UserVisitedCocktailsService,
    private val snapshotRepository: SnapshotRepository,
    private val commonCocktailListMapper: CocktailListMapper,
    private val tagsRepository: TagsRepository,
) : ComponentContext by componentContext {

    val state: StateFlow<UiState<VisitedCocktailList>> = flow {
        emit(UiState.Loading)
        val cocktailIds = visitedCocktailsService.getVisitedCocktails()
            .map { it.id }

        if (cocktailIds.isEmpty()) {
            emit(UiState.Data(VisitedCocktailList.Empty))
        } else {
            emit(UiState.Data(VisitedCocktailList.Cocktails(getCocktailsByIds(cocktailIds))))
        }
    }
        .flowOn(Dispatchers.Default)
        .stateInWhileSubscribe()

    private suspend fun getCocktailsByIds(ids: List<CocktailId>): List<CocktailsListState.Cocktails.Cocktail> {
        val cocktails = snapshotRepository.get().cocktails
            .filter { cocktailDto -> ids.contains(cocktailDto.id) }
            .sortedBy { cocktailDto -> ids.indexOf(cocktailDto.id) }
            .map { cocktailDto ->
                CocktailsProvider.Cocktail(
                    id = cocktailDto.id,
                    url = ImageUrlCreators.createUrl(
                        cocktailDto.id,
                        ImageUrlCreators.Size.SIZE_400
                    ),
                    name = cocktailDto.name,
                    tags = tagsRepository.getTags(cocktailDto.tags)
                )
            }

        return commonCocktailListMapper.map(cocktails)
    }

    sealed class VisitedCocktailList {
        data class Cocktails(val cocktails: List<CocktailsListState.Cocktails.Cocktail>) : VisitedCocktailList()

        object Empty : VisitedCocktailList()
    }

}

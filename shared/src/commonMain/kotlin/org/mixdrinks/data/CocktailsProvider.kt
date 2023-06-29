package org.mixdrinks.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.mixdrinks.domain.CocktailSelector
import org.mixdrinks.domain.ImageUrlCreators
import org.mixdrinks.dto.CocktailId
import org.mixdrinks.dto.TagDto
import org.mixdrinks.ui.list.FilterObserver

internal class CocktailsProvider(
    private val snapshotRepository: SnapshotRepository,
    private val filterRepository: FilterObserver,
    private val cocktailSelector: suspend () -> CocktailSelector,
    private val tagsRepository: TagsRepository,
) {

    data class Cocktail(
        val id: CocktailId,
        val url: String,
        val name: String,
        val tags: List<TagDto>,
    )

    suspend fun getCocktails(): Flow<List<Cocktail>> {
        return filterRepository.selected.map {
            val notEmptyFilter =
                it.filter { filterGroup -> filterGroup.value.isNotEmpty() }
            if (notEmptyFilter.isEmpty()) {
                snapshotRepository.get().cocktails
            } else {
                val notEmptyFilterIds = notEmptyFilter
                    .mapValues { filterGroupIdListEntry -> filterGroupIdListEntry.value.map { it.filterId } }

                val ids = cocktailSelector().getCocktailIds(notEmptyFilterIds)
                snapshotRepository.get().cocktails
                    .filter { cocktailDto -> ids.contains(cocktailDto.id) }
            }
                .map { cocktailDto ->
                    Cocktail(
                        id = cocktailDto.id,
                        url = ImageUrlCreators.createUrl(
                            cocktailDto.id,
                            ImageUrlCreators.Size.SIZE_400
                        ),
                        name = cocktailDto.name,
                        tags = tagsRepository.getTags(cocktailDto.tags)
                    )
                }
        }
    }

    suspend fun getAllCocktails(): List<Cocktail> {
        return snapshotRepository.get().cocktails
            .map { cocktailDto ->
                Cocktail(
                    id = cocktailDto.id,
                    url = ImageUrlCreators.createUrl(
                        cocktailDto.id,
                        ImageUrlCreators.Size.SIZE_400
                    ),
                    name = cocktailDto.name,
                    tags = tagsRepository.getTags(cocktailDto.tags)
                )
            }
    }
}

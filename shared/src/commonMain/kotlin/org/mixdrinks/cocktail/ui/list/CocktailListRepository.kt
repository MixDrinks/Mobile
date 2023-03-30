package org.mixdrinks.cocktail.ui.list

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.mixdrinks.cocktail.ui.filters.FilterRepository
import org.mixdrinks.domain.CocktailSelector
import org.mixdrinks.dto.CocktailDto
import org.mixdrinks.dto.SnapshotDto

class CocktailListRepository(
    private val snapshot: suspend () -> SnapshotDto,
    private val filterRepository: FilterRepository,
    private val cocktailSelector: suspend () -> CocktailSelector,
) {

  suspend fun getCocktails(): Flow<List<CocktailDto>> {
    return filterRepository.selected.map {
      val notEmptyFilter = it.filter { it.value.isNotEmpty() }
      if (notEmptyFilter.isEmpty()) {
        snapshot().cocktails
      } else {
        val notEmptyFilterIds = notEmptyFilter
            .mapValues { filterGroupIdListEntry -> filterGroupIdListEntry.value.map { it.filterId } }

        val ids = cocktailSelector().getCocktailIds(notEmptyFilterIds)
        snapshot().cocktails.filter { cocktailDto -> ids.contains(cocktailDto.id) }
      }
    }
  }
}

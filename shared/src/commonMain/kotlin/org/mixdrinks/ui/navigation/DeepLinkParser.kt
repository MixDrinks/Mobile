package org.mixdrinks.ui.navigation

import io.ktor.http.Url
import org.mixdrinks.domain.FilterGroups
import org.mixdrinks.domain.FilterPathParser
import org.mixdrinks.dto.FilterId
import org.mixdrinks.dto.SnapshotDto

internal class DeepLinkParser(
    private val snapshot: suspend () -> SnapshotDto,
    private val filterPathParser: FilterPathParser,
) {

    sealed class DeepLinkAction {
        data class Cocktail(val id: Int) : DeepLinkAction()
        data class Filters(val selectedFilters: Map<FilterGroups, List<FilterId>>) :
            DeepLinkAction()
    }

    suspend fun parseDeepLink(link: String): DeepLinkAction? {
        val cleanPath = Url(link).encodedPath.removePrefix("/")
        return when {
            cleanPath.startsWith("cocktails/") -> {
                val cocktailSlug = cleanPath.removePrefix("cocktails/")
                val cocktailId =
                    snapshot().cocktails.find { it.slug == cocktailSlug }?.id?.id ?: return null
                DeepLinkAction.Cocktail(cocktailId)
            }

            cleanPath.isFilterGroup() -> {
                val filters = filterPathParser.parse(cleanPath)

                val resultFilters = filters.mapNotNull { (group, filterSlugs) ->
                    val filterGroupFromSnapshot =
                        snapshot().filterGroups.find { it.id == group.id } ?: return@mapNotNull null

                    group to filterGroupFromSnapshot.filters
                        .filter { filterSlugs.contains(it.slug) }
                        .map { it.id }
                }.toMap()

                DeepLinkAction.Filters(resultFilters)
            }

            else -> null
        }
    }

    private fun String.isFilterGroup(): Boolean {
        return FilterGroups.values().find { this.startsWith(it.queryName.value) } != null
    }
}

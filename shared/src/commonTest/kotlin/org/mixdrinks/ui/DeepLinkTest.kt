package org.mixdrinks.ui

import kotlinx.coroutines.runBlocking
import org.mixdrinks.domain.FilterGroups
import org.mixdrinks.domain.FilterPathParser
import org.mixdrinks.dto.CocktailDto
import org.mixdrinks.dto.CocktailId
import org.mixdrinks.dto.FilterGroupDto
import org.mixdrinks.dto.FilterId
import org.mixdrinks.dto.FilterWithCocktailIdsDto
import org.mixdrinks.dto.GlasswareId
import org.mixdrinks.dto.SelectionType
import org.mixdrinks.dto.SnapshotDto
import kotlin.test.Test
import kotlin.test.assertEquals

class DeepLinkTest {

    @Test
    fun `verify parse deeplink not valid`() = runBlocking {
        val snapshotDto = createSnapshot()

        val deepLinkParser = DeepLinkParser({ snapshotDto }, FilterPathParser())

        assertEquals(null, deepLinkParser.parseDeepLink("cocktails"))
        assertEquals(null, deepLinkParser.parseDeepLink("aaaa"))
        assertEquals(null, deepLinkParser.parseDeepLink(""))
        assertEquals(null, deepLinkParser.parseDeepLink("v2"))
    }

    @Test
    fun `verify parse deeplink cocktails`() = runBlocking {
        val snapshotDto = createSnapshot()

        val deepLinkParser = DeepLinkParser({ snapshotDto }, FilterPathParser())

        val result = deepLinkParser.parseDeepLink("cocktails/50-vidtinkiv-ulunu")

        assertEquals(DeepLinkParser.DeepLinkAction.Cocktail(0), result)
    }

    @Test
    fun `verify parse deeplink cocktails start with slash`() = runBlocking {
        val snapshotDto = createSnapshot()

        val deepLinkParser = DeepLinkParser({ snapshotDto }, FilterPathParser())

        val result = deepLinkParser.parseDeepLink("/cocktails/1_cocktail")

        assertEquals(DeepLinkParser.DeepLinkAction.Cocktail(1), result)
    }

    @Test
    fun `verify parse deeplink cocktails start with slash with domain`() = runBlocking {
        val snapshotDto = createSnapshot()

        val deepLinkParser = DeepLinkParser({ snapshotDto }, FilterPathParser())

        val result = deepLinkParser.parseDeepLink("https://mixdrinks.org/cocktails/1_cocktail")

        assertEquals(DeepLinkParser.DeepLinkAction.Cocktail(1), result)
    }

    @Test
    fun `verify parse deeplink filter alcohol volume`() = runBlocking {
        val snapshotDto = createSnapshot()

        val deepLinkParser = DeepLinkParser({ snapshotDto }, FilterPathParser())

        val result = deepLinkParser.parseDeepLink("alcohol-volume=mitsni")

        assertEquals(
            DeepLinkParser.DeepLinkAction.Filters(
                mapOf(
                    FilterGroups.ALCOHOL_VOLUME to listOf(FilterId(100)),
                )
            ), result
        )
    }

    @Test
    fun `verify parse deeplink filter taste volume`() = runBlocking {
        val snapshotDto = createSnapshot()

        val deepLinkParser = DeepLinkParser({ snapshotDto }, FilterPathParser())

        val result = deepLinkParser.parseDeepLink("taste=solodki")

        assertEquals(
            DeepLinkParser.DeepLinkAction.Filters(
                mapOf(
                    FilterGroups.TASTE to listOf(FilterId(200)),
                )
            ), result
        )
    }

    @Test
    fun `verify parse deeplink filter taste volume many group many filters`() = runBlocking {
        val snapshotDto = createSnapshot()

        val deepLinkParser = DeepLinkParser({ snapshotDto }, FilterPathParser())

        val result = deepLinkParser.parseDeepLink("alcohol-volume=mitsni/taste=solodki")

        assertEquals(
            DeepLinkParser.DeepLinkAction.Filters(
                mapOf(
                    FilterGroups.ALCOHOL_VOLUME to listOf(FilterId(100)),
                    FilterGroups.TASTE to listOf(FilterId(200)),
                )
            ), result
        )
    }

    @Suppress("LongMethod")
    private fun createSnapshot(): SnapshotDto {
        return SnapshotDto(
            cocktails = listOf(
                CocktailDto(
                    id = CocktailId(0),
                    name = "Cocktail 0",
                    receipt = emptyList(),
                    goods = emptyList(),
                    tools = emptyList(),
                    tags = emptyList(),
                    tastes = emptyList(),
                    glassware = GlasswareId(-1),
                    slug = "50-vidtinkiv-ulunu",
                ),
                CocktailDto(
                    id = CocktailId(1),
                    name = "Cocktail 1",
                    receipt = emptyList(),
                    goods = emptyList(),
                    tools = emptyList(),
                    tags = emptyList(),
                    tastes = emptyList(),
                    glassware = GlasswareId(-1),
                    slug = "1_cocktail",
                )
            ),
            tools = emptyList(),
            goods = emptyList(),
            tags = emptyList(),
            tastes = emptyList(),
            filterGroups = listOf(
                FilterGroupDto(
                    id = FilterGroups.ALCOHOL_VOLUME.id,
                    name = "",
                    filters = listOf(
                        FilterWithCocktailIdsDto(
                            id = FilterId(100),
                            name = "",
                            cocktailIds = emptySet(),
                            slug = "mitsni",
                        ),
                    ),
                    slug = "alcohol-volume",
                    selectionType = SelectionType.SINGLE,
                ),
                FilterGroupDto(
                    id = FilterGroups.TASTE.id,
                    name = "",
                    filters = listOf(
                        FilterWithCocktailIdsDto(
                            id = FilterId(200),
                            name = "",
                            cocktailIds = emptySet(),
                            slug = "solodki",
                        ),
                    ),
                    slug = "taste",
                    selectionType = SelectionType.SINGLE,
                ),
            ),
            glassware = emptyList(),
        )
    }
}

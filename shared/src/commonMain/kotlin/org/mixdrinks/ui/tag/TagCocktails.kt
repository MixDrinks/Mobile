package org.mixdrinks.ui.tag

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import org.mixdrinks.ui.list.cocktailListInserter
import org.mixdrinks.ui.widgets.MixDrinksHeader

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun TagCocktails(
    component: CommonTagCocktailsComponent
) {
    val name by component.name.collectAsState()
    val cocktails by component.state.collectAsState()

    LazyColumn {
        stickyHeader {
            MixDrinksHeader(
                name = name,
                component::back
            )
        }

        cocktailListInserter(
            cocktails,
            component::navigateToDetails,
            component::navigateToTagCocktails
        )
    }
}

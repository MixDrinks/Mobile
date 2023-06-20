package org.mixdrinks.ui.tag

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import org.mixdrinks.ui.list.cocktailListInserter
import org.mixdrinks.ui.widgets.Header
import org.mixdrinks.ui.widgets.undomain.ComponentWidget

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun TagCocktails(
    componentProvider: suspend () -> CommonTagCocktailsComponent
) {
    ComponentWidget(componentProvider) { component ->
        val name by component.name.collectAsState()
        val cocktails by component.state.collectAsState()

        LazyColumn {
            stickyHeader {
                Header(
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
}

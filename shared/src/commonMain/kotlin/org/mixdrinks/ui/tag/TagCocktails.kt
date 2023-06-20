package org.mixdrinks.ui.tag

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import kotlinx.coroutines.flow.map
import org.mixdrinks.ui.list.cocktailListInserter
import org.mixdrinks.ui.widgets.Header

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun TagCocktails(
    component: CommonTagCocktailsComponent
) {
    val name by component.name.collectAsState()

    val cocktails by component.state.collectAsState()

    LazyColumn {
        stickyHeader {
            Header(
                name = name,
                {}
            )
        }

        cocktailListInserter(cocktails, component::navigateToDetails)
    }
}

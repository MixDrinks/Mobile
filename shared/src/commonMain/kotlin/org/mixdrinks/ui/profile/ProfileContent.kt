package org.mixdrinks.ui.profile

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import org.mixdrinks.ui.list.cocktailListInserter
import org.mixdrinks.ui.widgets.MixDrinksHeader
import org.mixdrinks.ui.widgets.undomain.ContentHolder

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun ProfileContent(component: ProfileComponent) {
    ContentHolder(
        stateflow = component.state,
    ) { cocktails ->
        LazyColumn {
            stickyHeader {
                MixDrinksHeader(
                    name = "Переглянуті коктейлі",
                    {}
                )
            }

            cocktailListInserter(
                cocktails,
                {}, {}
            )
        }
    }
}

package org.mixdrinks.ui.profile

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.mixdrinks.app.styles.MixDrinksTextStyles
import org.mixdrinks.ui.list.cocktailListInserter
import org.mixdrinks.ui.widgets.MixDrinksHeader
import org.mixdrinks.ui.widgets.undomain.ContentHolder

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun ProfileContent(component: ProfileComponent) {
    ContentHolder(
        stateflow = component.state,
    ) { cocktails ->

        when (cocktails) {
            is ProfileComponent.VisitedCocktailList.Cocktails -> {
                LazyColumn {
                    stickyHeader {
                        MixDrinksHeader(
                            name = "Переглянуті коктейлі",
                            onBackClick = null,
                        )
                    }

                    cocktailListInserter(
                        cocktails.cocktails,
                        {}, {}
                    )
                }
            }

            ProfileComponent.VisitedCocktailList.Empty -> Box(
                modifier = Modifier.fillMaxSize()
            ) {
                Text(
                    modifier = Modifier
                        .padding(16.dp)
                        .align(Alignment.Center),
                    textAlign = TextAlign.Center,
                    text = "Тут будуть переглянуті вами коктейлі",
                    style = MixDrinksTextStyles.H1,
                )
            }
        }
    }
}

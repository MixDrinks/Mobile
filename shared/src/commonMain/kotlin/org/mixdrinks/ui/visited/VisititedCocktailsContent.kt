package org.mixdrinks.ui.visited

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.mixdrinks.app.styles.MixDrinksColors
import org.mixdrinks.app.styles.MixDrinksTextStyles
import org.mixdrinks.ui.list.cocktailListInserter
import org.mixdrinks.ui.widgets.undomain.ContentHolder

@Composable
internal fun VisititedCocktailsContent(component: VisitedCocktailsComponent) {
    Column {
        Row(
            modifier = Modifier
                .background(MixDrinksColors.Main)
                .fillMaxWidth()
                .height(52.dp),
        ) {
            Text(
                modifier = Modifier
                    .padding(start = 4.dp)
                    .align(Alignment.CenterVertically),
                color = MixDrinksColors.White,
                text = "Переглянуті коктейлі",
                style = MixDrinksTextStyles.H2,
                softWrap = false,
                maxLines = 1,
            )

            Spacer(modifier = Modifier.weight(1f))

            Text(
                modifier = Modifier
                    .clickable {
                        component.logout()
                    }
                    .align(Alignment.CenterVertically)
                    .padding(end = 4.dp),
                text = "Вийти",
                style = MixDrinksTextStyles.H4,
                color = MixDrinksColors.White,
            )
        }

        ContentHolder(
            stateflow = component.state,
        ) { cocktails ->
            when (cocktails) {
                is VisitedCocktailsComponent.VisitedCocktailList.Cocktails -> {
                    LazyColumn {
                        cocktailListInserter(
                            cocktails.cocktails,
                            component::navigateToDetails,
                            component::navigateToTagCocktails,
                        )
                    }
                }

                VisitedCocktailsComponent.VisitedCocktailList.Empty -> Box(
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
}

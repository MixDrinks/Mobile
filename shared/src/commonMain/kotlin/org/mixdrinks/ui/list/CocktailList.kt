package org.mixdrinks.ui.list

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.seiko.imageloader.rememberAsyncImagePainter
import org.mixdrinks.app.styles.MixDrinksColors
import org.mixdrinks.app.styles.MixDrinksTextStyles
import org.mixdrinks.data.Tracking
import org.mixdrinks.dto.CocktailId
import org.mixdrinks.dto.TagId
import org.mixdrinks.ui.tag.Tag

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun CocktailList(
    cocktails: CocktailsListState.Cocktails,
    onClick: (CocktailId) -> Unit,
    onTagClick: (TagId) -> Unit,
    trackingScreen: String,
) {
    LazyColumn {
        items(cocktails.list, key = { cocktail -> cocktail.id.id }) { cocktail ->
            Cocktail(
                modifier = Modifier.animateItemPlacement(),
                cocktail = cocktail,
                onClick = onClick,
                onTagClick = onTagClick,
                trackingScreen = trackingScreen
            )
        }
    }
}

internal fun LazyListScope.cocktailListInserter(
    cocktails: CocktailsListState.Cocktails,
    onClick: (CocktailId) -> Unit,
    onTagClick: (TagId) -> Unit,
    trackingScreen: String,
) {
    cocktails.list.forEach {
        item(key = it.id.id) {
            Cocktail(
                modifier = Modifier,
                cocktail = it,
                onClick = onClick,
                onTagClick = onTagClick,
                trackingScreen = trackingScreen
            )
        }
    }
}

internal fun LazyListScope.cocktailListInserter(
    cocktails: List<CocktailsListState.Cocktails.Cocktail>,
    onClick: (CocktailId) -> Unit,
    onTagClick: (TagId) -> Unit,
    trackingScreen: String,
) {
    cocktails.forEach {
        item(key = it.id.id) {
            Cocktail(
                modifier = Modifier,
                cocktail = it,
                onClick = onClick,
                onTagClick = onTagClick,
                trackingScreen = trackingScreen
            )
        }
    }
}


@Composable
internal fun Cocktail(
    modifier: Modifier,
    cocktail: CocktailsListState.Cocktails.Cocktail,
    onClick: (CocktailId) -> Unit,
    onTagClick: (TagId) -> Unit,
    trackingScreen: String,
) {
    Card(
        modifier = modifier
            .height(100.dp)
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        shape = RoundedCornerShape(8.dp),
    ) {
        Row(modifier = Modifier.clickable {
            Tracking.track(
                action = "open_cocktail_details",
                screen = trackingScreen,
                data = mapOf("cocktail_name" to cocktail.name)
            )
            onClick(cocktail.id)
        }) {
            Image(
                painter = rememberAsyncImagePainter(cocktail.url),
                contentDescription = "Коктейль ${cocktail.name}",
                contentScale = ContentScale.FillHeight,
                modifier = Modifier.width(100.dp),
            )

            Column {
                Text(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    text = cocktail.name,
                    color = MixDrinksColors.Main,
                    style = MixDrinksTextStyles.H4,
                )

                LazyRow(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(cocktail.tags) {
                        Tag(it, onTagClick)
                    }
                }
            }
        }
    }
}

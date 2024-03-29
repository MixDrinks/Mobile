package org.mixdrinks.ui.items

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.seiko.imageloader.rememberAsyncImagePainter
import org.mixdrinks.app.styles.MixDrinksColors
import org.mixdrinks.app.styles.MixDrinksTextStyles
import org.mixdrinks.data.DetailGoodsUiModel
import org.mixdrinks.ui.list.cocktailListInserter
import org.mixdrinks.ui.widgets.MixDrinksHeader
import org.mixdrinks.ui.widgets.undomain.ContentHolder

@Composable
internal fun ItemDetailsView(component: ItemDetailComponent) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MixDrinksColors.White),
    ) {
        ContentHolder(
            stateflow = component.state
        ) {
            ItemViewContent(it, component)
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun ItemViewContent(
    good: DetailGoodsUiModel,
    component: ItemDetailComponent,
) {
    val predefineComponent = remember(good) { component.predefineCocktailComponent }
    val cocktails by predefineComponent.state.collectAsState()
    LazyColumn {
        stickyHeader {
            MixDrinksHeader(good.name, component::back)
        }
        goodsViewScrollContent(Modifier.padding(horizontal = 8.dp), good)
        item {
            Text(
                modifier = Modifier.padding(8.dp),
                style = MixDrinksTextStyles.H2,
                text = "Коктейлі з ${good.name}",
            )
        }
        cocktailListInserter(
            cocktails = cocktails,
            onClick = predefineComponent::navigateToDetails,
            onTagClick = predefineComponent::navigateToTagCocktails,
            trackingScreen = "page_item_details"
        )
    }
}

internal fun LazyListScope.goodsViewScrollContent(modifier: Modifier, good: DetailGoodsUiModel) {
    item {
        Image(
            painter = rememberAsyncImagePainter(good.url),
            contentDescription = good.name,
            contentScale = ContentScale.FillHeight,
            modifier = modifier.fillMaxWidth()
                .padding(top = 8.dp, bottom = 24.dp)
                .height(300.dp),
        )
    }
    item {
        Text(
            modifier = modifier,
            style = MixDrinksTextStyles.H4,
            text = good.about,
        )
    }
}

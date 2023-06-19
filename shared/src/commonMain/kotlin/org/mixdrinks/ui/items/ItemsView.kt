package org.mixdrinks.ui.items

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.seiko.imageloader.rememberAsyncImagePainter
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource
import org.mixdrinks.app.styles.MixDrinksColors
import org.mixdrinks.app.styles.MixDrinksTextStyles
import org.mixdrinks.data.DetailItemsUiModel
import org.mixdrinks.ui.widgets.undomain.ContentHolder

@Composable
internal fun AccessoriesView(component: ItemsComponent) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MixDrinksColors.White),
    ) {
        ContentHolder(
            stateflow = component.state
        ) {
            GoodsViewContent(it, component)
        }
    }
}

@OptIn(ExperimentalResourceApi::class)
@Composable
internal fun GoodsViewContent(
    item: DetailItemsUiModel,
    component: ItemsComponent
) {
    Column {
        Row(
            modifier = Modifier
                .background(MixDrinksColors.Main)
                .fillMaxWidth()
                .height(52.dp),
        ) {
            Box(
                modifier = Modifier.size(52.dp)
                    .clickable {
                        component.close()
                    }
            ) {
                Image(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(32.dp)
                        .padding(start = 12.dp),
                    painter = painterResource("ic_arrow_back.xml"),
                    contentDescription = "Test"
                )
            }
            Text(
                modifier = Modifier.padding(start = 4.dp)
                    .align(Alignment.CenterVertically),
                color = MixDrinksColors.White,
                text = item.name,
                style = MixDrinksTextStyles.H2,
                softWrap = false,
                maxLines = 1,
            )
        }
        Spacer(modifier = Modifier.height(4.dp).fillMaxWidth())
        GoodsViewScrollContent(Modifier, item)
    }
}

@Composable
internal fun GoodsViewScrollContent(
    modifier: Modifier,     item: DetailItemsUiModel
) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .fillMaxWidth(1f)
            .fillMaxHeight(1f)
            .padding(10.dp)
    ) {
        Spacer(modifier = modifier.padding(5.dp))
        Image(
            painter = rememberAsyncImagePainter(item.url),
            contentDescription = "Продукт ${item.name}",
            contentScale = ContentScale.FillHeight,
            modifier = Modifier.fillMaxWidth()
                .padding(vertical = 8.dp)
                .height(300.dp),
        )

        Spacer(modifier = modifier.padding(top = 20.dp))

        Row(
            modifier = modifier
                .fillMaxWidth(1f),
            horizontalArrangement = Arrangement.Start
        ) {
            Text(
                style = MixDrinksTextStyles.H2,
                text = "Опис ${item.name}"
            )
        }
        Spacer(modifier = modifier.padding(15.dp))

        Row(
            modifier = modifier
                .fillMaxWidth(1f),
            horizontalArrangement = Arrangement.Start
        ) {
            Text(
                style = MixDrinksTextStyles.H4,
                text = item.about,
            )
        }
    }
}


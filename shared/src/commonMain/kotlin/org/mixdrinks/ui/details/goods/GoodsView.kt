package org.mixdrinks.ui.details.goods

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.seiko.imageloader.rememberAsyncImagePainter
import org.mixdrinks.app.styles.MixDrinksColors
import org.mixdrinks.app.styles.MixDrinksTextStyles
import org.mixdrinks.dto.GoodId
import org.mixdrinks.ui.widgets.undomain.UiState

@Composable
internal fun GoodsView(
    goodsSubComponent: GoodsSubComponent,
    onGoodClick: (goodId: GoodId) -> Unit
) {

    val state by goodsSubComponent.state.collectAsState()

    if (state is UiState.Data) {
        val safeState = (state as UiState.Data<GoodsSubComponent.GoodsUi>).data
        Column {
            Box(modifier = Modifier.fillMaxWidth()) {
                Text(
                    modifier = Modifier
                        .padding(start = 12.dp, bottom = 12.dp)
                        .align(Alignment.CenterStart),
                    color = MixDrinksColors.Black,
                    text = "Інгрідієнти",
                    style = MixDrinksTextStyles.H1,
                )
                Box(
                    modifier = Modifier
                        .padding(horizontal = 12.dp)
                        .align(Alignment.CenterEnd)
                ) {
                    Counter(
                        count = safeState.count,
                        onPlus = goodsSubComponent::onPlusClick,
                        onMinus = goodsSubComponent::onMinusClick,
                    )
                }
            }

            safeState.goods.forEach {
                Good(it, onGoodClick)
            }
        }
    }
}

@Composable
internal fun Good(good: GoodsSubComponent.GoodUi, onGoodClick: (goodId: GoodId) -> Unit) {
    Card(
        modifier = Modifier
            .clickable { onGoodClick(good.goodId) }
            .height(64.dp)
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 2.dp)
            .border(
                1.dp,
                MixDrinksColors.Main,
                shape = RoundedCornerShape(8.dp),
            ),
        shape = RoundedCornerShape(8.dp),
    ) {
        Row {
            Image(
                painter = rememberAsyncImagePainter(good.url),
                contentDescription = good.name,
                contentScale = ContentScale.FillWidth,
                modifier = Modifier.size(64.dp)
            )
            Text(
                modifier = Modifier.align(Alignment.CenterVertically)
                    .padding(horizontal = 8.dp),
                text = good.name,
                style = MixDrinksTextStyles.H5,
            )
            Spacer(Modifier.weight(1f).fillMaxHeight())
            Text(
                modifier = Modifier.align(Alignment.CenterVertically)
                    .padding(horizontal = 8.dp),
                text = good.amount,
                style = MixDrinksTextStyles.H5,
            )
        }
    }
}

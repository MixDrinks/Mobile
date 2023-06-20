package org.mixdrinks.ui.widgets

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource
import org.mixdrinks.app.styles.MixDrinksColors
import org.mixdrinks.app.styles.MixDrinksTextStyles

@OptIn(ExperimentalResourceApi::class)
@Composable
internal fun MixDrinksHeader(name: String, onBackClick: () -> Unit) {
    Row(
        modifier = Modifier
            .background(MixDrinksColors.Main)
            .fillMaxWidth()
            .height(52.dp),
    ) {
        Box(
            modifier = Modifier.size(52.dp)
                .clickable {
                    onBackClick()
                }
        ) {
            Image(
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(32.dp)
                    .padding(start = 12.dp),
                painter = painterResource("ic_arrow_back.xml"),
                contentDescription = "Назад"
            )
        }
        Text(
            modifier = Modifier.padding(start = 4.dp)
                .align(Alignment.CenterVertically),
            color = MixDrinksColors.White,
            text = name,
            style = MixDrinksTextStyles.H2,
            softWrap = false,
            maxLines = 1,
        )
    }
}

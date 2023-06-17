package org.mixdrinks.ui.details.goods

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource
import org.mixdrinks.app.styles.MixDrinksColors
import org.mixdrinks.app.styles.MixDrinksTextStyles

@Composable
internal fun Counter(
    count: Int,
    onPlus: () -> Unit,
    onMinus: () -> Unit,
) {

    val counterHeight = 40.dp
    Row {
        ChangeCountButton(
            counterHeight = counterHeight,
            resource = "ic_minus.xml",
            contentDescription = "Менше",
            onClick = onMinus
        )

        Spacer(
            modifier = Modifier.width(4.dp)
        )

        Box(
            modifier = Modifier
                .size(counterHeight)
                .border(1.dp, Color.Black, RoundedCornerShape(4.dp)),
        ) {
            Text(
                modifier = Modifier.align(Alignment.Center),
                text = count.toString(),
                style = MixDrinksTextStyles.H4,
                color = MixDrinksColors.Black,
            )
        }

        Spacer(
            modifier = Modifier.width(4.dp)
        )

        ChangeCountButton(
            counterHeight = counterHeight,
            resource = "ic_plus.xml",
            contentDescription = "Більше",
            onClick = onPlus
        )

        Spacer(
            modifier = Modifier.width(4.dp)
        )
    }
}

@OptIn(ExperimentalResourceApi::class)
@Composable
internal fun ChangeCountButton(
    counterHeight: Dp,
    resource: String,
    contentDescription: String,
    onClick: () -> Unit,
) {
    Button(
        colors = ButtonDefaults.buttonColors(backgroundColor = MixDrinksColors.Main),
        onClick = onClick,
        contentPadding = PaddingValues(0.dp),
        shape = RoundedCornerShape(4.dp),
        modifier = Modifier
            .size(counterHeight)
    ) {
        Image(
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .padding(4.dp)
                .fillMaxSize(),
            painter = painterResource(resource),
            contentDescription = contentDescription,
            contentScale = ContentScale.Fit,
        )
    }
}

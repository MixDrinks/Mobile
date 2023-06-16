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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource
import org.mixdrinks.app.styles.MixDrinksColors
import org.mixdrinks.app.styles.MixDrinksTextStyles

@OptIn(ExperimentalResourceApi::class)
@Composable
internal fun Counter(
    count: Int,
    onPlus: () -> Unit,
    onMinus: () -> Unit,
) {
  val counterHeight = 40.dp
  Row {
    Button(
        colors = ButtonDefaults.buttonColors(backgroundColor = MixDrinksColors.Main),
        onClick = onMinus,
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
              painter = painterResource("minus.xml"),
              contentDescription = "Менше",
              contentScale = ContentScale.Fit,
      )
    }

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

    Button(
        colors = ButtonDefaults.buttonColors(backgroundColor = MixDrinksColors.Main),
        onClick = onPlus,
        contentPadding = PaddingValues(0.dp),
        shape = RoundedCornerShape(4.dp),
        modifier = Modifier
            .size(counterHeight)
    ) {
      Text("+", color = MixDrinksColors.White, fontSize = 28.sp)
    }

    Spacer(
        modifier = Modifier.width(4.dp)
    )
  }
}

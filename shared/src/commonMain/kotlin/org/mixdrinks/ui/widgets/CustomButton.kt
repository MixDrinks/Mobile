package org.mixdrinks.ui.widgets

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.mixdrinks.app.styles.MixDrinksColors
import org.mixdrinks.app.styles.MixDrinksTextStyles

@Composable
internal fun CustomButton(modifier: Modifier, text: String, onClick: () -> Unit) {
  Button(
      onClick = { onClick() },
      shape = RoundedCornerShape(16.dp),
      colors = ButtonDefaults.buttonColors(backgroundColor = MixDrinksColors.Main),
      modifier = modifier
          .fillMaxWidth()
          .padding(vertical = 8.dp)
          .height(40.dp)
  ) {
    Text(
        text = text,
        style = MixDrinksTextStyles.H4,
        color = MixDrinksColors.White
    )
  }
}

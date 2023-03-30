package org.mixdrinks.ui.widgets

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import org.mixdrinks.app.styles.MixDrinksColors

@Composable
fun Loader() {
  Box(
      modifier = Modifier.fillMaxSize()
  ) {
    CircularProgressIndicator(
        color = MixDrinksColors.Main,
        modifier = Modifier.align(Alignment.Center)
    )
  }
}

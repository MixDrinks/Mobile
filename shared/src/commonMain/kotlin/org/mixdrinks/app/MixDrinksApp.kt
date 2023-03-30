package org.mixdrinks.app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.arkivanov.decompose.ComponentContext
import org.mixdrinks.ui.RootContent

@Composable
internal fun MixDrinksApp(context: ComponentContext) {
  val rootComponent = remember { org.mixdrinks.ui.RootComponent(context) }
  RootContent(rootComponent)
}

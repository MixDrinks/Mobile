package org.mixdrinks

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.arkivanov.decompose.ComponentContext
import org.mixdrinks.cocktail.ui.RootComponent
import org.mixdrinks.cocktail.ui.RootContent

@Composable
internal fun MixDrinksApp(context: ComponentContext) {
  val rootComponent = remember("Key") { RootComponent(context) }
  RootContent(rootComponent)
}

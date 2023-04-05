package org.mixdrinks.app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import org.mixdrinks.ui.RootComponent
import org.mixdrinks.ui.RootContent

@Composable
internal fun MixDrinksApp() {
  val rootComponent = remember {
    val lifecycle = LifecycleRegistry()
    RootComponent(DefaultComponentContext(lifecycle))
  }
  RootContent(rootComponent)
}

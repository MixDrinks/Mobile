package org.mixdrinks.cocktail.ui.widgets.undomain

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.lifecycle.doOnDestroy
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

fun ComponentContext.launch(block: suspend () -> Unit) {
  val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())
  lifecycle.doOnDestroy {
    scope.cancel()
  }
  scope.launch {
    block()
  }
}

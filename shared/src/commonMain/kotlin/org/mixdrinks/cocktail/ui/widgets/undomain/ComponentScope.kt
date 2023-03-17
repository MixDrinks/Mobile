package org.mixdrinks.cocktail.ui.widgets.undomain

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.instancekeeper.InstanceKeeper
import com.arkivanov.essenty.lifecycle.Lifecycle
import com.arkivanov.essenty.lifecycle.doOnDestroy
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

private const val INSTANCE_KEY =
    "org.mixdrinks.cocktail.ui.widgets.undomain.ComponentContextScope.INSTANCE_KEY"

fun ComponentContext.launch(block: suspend () -> Unit) {
  scope.launch {
    block()
  }
}

val ComponentContext.scope: CoroutineScope
  get() {
    val scope = instanceKeeper.get(INSTANCE_KEY)
    if (scope is CoroutineScope) return scope

    return DestroyableCoroutineScope(SupervisorJob() + Dispatchers.Default).also {
      instanceKeeper.put(INSTANCE_KEY, it)
    }
  }

class DestroyableCoroutineScope(
    context: CoroutineContext,
) : CoroutineScope, InstanceKeeper.Instance {

  override val coroutineContext: CoroutineContext = context

  override fun onDestroy() {
    coroutineContext.cancel()
  }
}

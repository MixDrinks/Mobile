package org.mixdrinks.cocktail.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.SaveableStateHolder
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.Child
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.value.Value
import org.mixdrinks.cocktail.ui.details.DetailView
import org.mixdrinks.cocktail.ui.filters.FilterView
import org.mixdrinks.cocktail.ui.list.CocktailListView
import org.mixdrinks.styles.MixDrinksColors

@Composable
fun RootContent(component: RootComponent) {
  Children(
      modifier = Modifier.background(MixDrinksColors.White),
      stack = component.stack,
  ) {

    when (val child = it.instance) {
      is RootComponent.Child.List -> CocktailListView(child.component)
      is RootComponent.Child.Details -> DetailView(child.component)
      is RootComponent.Child.Filters -> FilterView(child.component)
    }
  }
}

@Composable
fun <C : Any, T : Any> Children(
    stack: ChildStack<C, T>,
    modifier: Modifier = Modifier,
    content: @Composable (child: Child.Created<C, T>) -> Unit,
) {
  val holder = rememberSaveableStateHolder()

  holder.retainStates(stack.getConfigurations())

  val anim = emptyStackAnimation<C, T>()

  anim(stack = stack, modifier = modifier) { child ->
    holder.SaveableStateProvider(child.configuration.key()) {
      content(child)
    }
  }
}

internal fun <C : Any, T : Any> emptyStackAnimation(): StackAnimation<C, T> =
    StackAnimation { stack, modifier, childContent ->
      Box(modifier = modifier) {
        childContent(stack.active)
      }
    }

fun interface StackAnimation<C : Any, T : Any> {

  @Composable
  operator fun invoke(
      stack: ChildStack<C, T>,
      modifier: Modifier,
      content: @Composable (child: Child.Created<C, T>) -> Unit,
  )
}

@Composable
fun <C : Any, T : Any> Children(
    stack: Value<ChildStack<C, T>>,
    modifier: Modifier = Modifier,
    content: @Composable (child: Child.Created<C, T>) -> Unit,
) {
  val state = stack.subscribeAsState()

  Children(
      stack = state.value,
      modifier = modifier,
      content = content
  )
}

private fun ChildStack<*, *>.getConfigurations(): Set<String> =
    items.mapTo(HashSet()) { it.configuration.key() }

private fun Any.key(): String = "${this::class.simpleName}_${hashCode().toString(radix = 36)}"

@Composable
private fun SaveableStateHolder.retainStates(currentKeys: Set<Any>) {
  val keys = remember(this) { Keys(currentKeys) }

  DisposableEffect(this, currentKeys) {
    keys.set.forEach {
      if (it !in currentKeys) {
        removeState(it)
      }
    }

    keys.set = currentKeys

    onDispose {}
  }
}

private class Keys(
    var set: Set<Any>,
)

@Composable
fun <T : Any> Value<T>.subscribeAsState(): State<T> {
  val state = remember(this) { mutableStateOf(value) }

  DisposableEffect(this) {
    val observer: (T) -> Unit = { state.value = it }

    subscribe(observer)

    onDispose {
      unsubscribe(observer)
    }
  }

  return state
}

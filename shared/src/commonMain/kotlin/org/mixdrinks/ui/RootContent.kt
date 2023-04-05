package org.mixdrinks.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import org.mixdrinks.app.styles.MixDrinksColors
import org.mixdrinks.ui.details.DetailView
import org.mixdrinks.ui.filters.main.FilterView
import org.mixdrinks.ui.filters.search.SearchItemView
import org.mixdrinks.ui.list.CocktailListView

@Composable
internal fun RootContent(component: RootComponent) {
  Box(Modifier.background(MixDrinksColors.White)) {
    val state by component.stack.collectAsState()

    when (val child = state?.active?.instance) {
      is RootComponent.Child.List -> CocktailListView(child.component)
      is RootComponent.Child.Details -> DetailView(child.component)
      is RootComponent.Child.Filters -> FilterView(child.component)
      is RootComponent.Child.ItemSearch -> SearchItemView(child.component)
      else -> {
        println("Unknown child: $child")
      }
    }
  }
}

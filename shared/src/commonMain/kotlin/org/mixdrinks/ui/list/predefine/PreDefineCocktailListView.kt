package org.mixdrinks.ui.list.predefine

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import org.mixdrinks.ui.list.CocktailList
import org.mixdrinks.ui.list.CocktailsListState
import org.mixdrinks.ui.widgets.undomain.ContentHolder

@Composable
internal fun PreDefineCocktailListView(component: PreDefineCocktailsComponent) {
    ContentHolder(
        stateflow = component.state,
    ) {
        when (it) {
            is CocktailsListState.PlaceHolder -> {
                Text("Something went wrong")
            }

            is CocktailsListState.Cocktails -> {
                CocktailList(it, component::onCocktailClick)
            }
        }
    }
}


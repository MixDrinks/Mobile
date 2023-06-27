package org.mixdrinks.ui.main

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.arkivanov.decompose.extensions.compose.jetbrains.stack.Children
import com.arkivanov.decompose.extensions.compose.jetbrains.stack.animation.slide
import com.arkivanov.decompose.extensions.compose.jetbrains.stack.animation.stackAnimation
import org.mixdrinks.ui.details.DetailView
import org.mixdrinks.ui.filters.main.FilterView
import org.mixdrinks.ui.filters.search.SearchItemView
import org.mixdrinks.ui.items.ItemDetailsView
import org.mixdrinks.ui.list.main.MutableCocktailList
import org.mixdrinks.ui.tag.TagCocktails

@Composable
internal fun MainContent(component: MainComponent, deepLink: String?) {
    Box {
        Children(
            stack = component.stack,
            animation = stackAnimation(
                animator = slide()
            ),
            content = {
                when (val child = it.instance) {
                    is MainComponent.Child.List -> MutableCocktailList(child.component)
                    is MainComponent.Child.Item -> ItemDetailsView(child.component)
                    is MainComponent.Child.Details -> DetailView(child.component)
                    is MainComponent.Child.Filters -> FilterView(child.component)
                    is MainComponent.Child.ItemSearch -> SearchItemView(child.component)
                    is MainComponent.Child.CommonTagCocktails -> TagCocktails(child.component)
                }
            }
        )
    }
    LaunchedEffect(deepLink) {
        if (deepLink != null) {
            component.onDeepLink(deepLink)
        }
    }
}

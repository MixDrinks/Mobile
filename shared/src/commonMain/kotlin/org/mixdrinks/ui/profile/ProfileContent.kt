package org.mixdrinks.ui.profile

import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import com.arkivanov.decompose.extensions.compose.jetbrains.stack.Children
import com.arkivanov.decompose.extensions.compose.jetbrains.stack.animation.slide
import com.arkivanov.decompose.extensions.compose.jetbrains.stack.animation.stackAnimation
import org.mixdrinks.ui.details.DetailView
import org.mixdrinks.ui.items.ItemDetailsView
import org.mixdrinks.ui.tag.TagCocktails
import org.mixdrinks.ui.visited.VisititedCocktailsContent

@Composable
internal fun ProfileContent(component: ProfileComponent) {
    Scaffold(
        content = {
            Children(
                stack = component.stack,
                animation = stackAnimation(
                    animator = slide()
                ),
                content = {
                    when (val child = it.instance) {
                        is ProfileComponent.ProfileChild.CommonTag -> TagCocktails(child.component)
                        is ProfileComponent.ProfileChild.Details -> DetailView(child.component)
                        is ProfileComponent.ProfileChild.Item -> ItemDetailsView(child.component)
                        is ProfileComponent.ProfileChild.VisitedCocktails -> VisititedCocktailsContent(child.component)
                    }
                }
            )
        }
    )
}

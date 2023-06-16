package org.mixdrinks.app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.arkivanov.decompose.DefaultComponentContext
import org.mixdrinks.ui.RootComponent
import org.mixdrinks.ui.RootContent

@Composable
internal fun MixDrinksApp(contextComponent: DefaultComponentContext) {
    val rootComponent = remember {
        RootComponent(contextComponent)
    }
    RootContent(rootComponent)
}

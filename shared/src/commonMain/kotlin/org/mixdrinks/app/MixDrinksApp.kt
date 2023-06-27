package org.mixdrinks.app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.arkivanov.decompose.DefaultComponentContext
import org.mixdrinks.di.Graph
import org.mixdrinks.ui.RootComponent
import org.mixdrinks.ui.RootContent

@Composable
internal fun MixDrinksApp(contextComponent: DefaultComponentContext, deepLink: String?) {
    val mainComponent = remember {
        val graph = Graph()
        RootComponent(contextComponent, graph)
    }

    RootContent(mainComponent, deepLink)
}

package org.mixdrinks.app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.arkivanov.decompose.DefaultComponentContext
import org.mixdrinks.di.ComponentsFactory
import org.mixdrinks.di.Graph
import org.mixdrinks.ui.RootComponent
import org.mixdrinks.ui.RootContent

@Composable
internal fun MixDrinksApp(contextComponent: DefaultComponentContext, deepLink: String?) {
    val rootComponent = remember {
        val graph = Graph()
        RootComponent(contextComponent, graph, ComponentsFactory(graph))
    }

    RootContent(rootComponent, deepLink)
}

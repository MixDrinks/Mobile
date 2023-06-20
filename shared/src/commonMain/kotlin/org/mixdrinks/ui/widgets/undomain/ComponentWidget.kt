package org.mixdrinks.ui.widgets.undomain

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import kotlinx.coroutines.flow.MutableStateFlow

@Composable
fun <T> ComponentWidget(
    provider: suspend () -> T,
    content: @Composable (T) -> Unit
) {
    val state = remember { MutableStateFlow<T?>(null) }

    LaunchedEffect("") {
        state.emit(provider())
    }

    val stateTest by state.collectAsState()

    stateTest?.let {
        content(it)
    }
}

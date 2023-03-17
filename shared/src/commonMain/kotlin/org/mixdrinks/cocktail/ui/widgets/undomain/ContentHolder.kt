package org.mixdrinks.cocktail.ui.widgets.undomain

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.StateFlow
import org.mixdrinks.cocktail.ui.widgets.Loader

@Composable
fun <T> ContentHolder(
    stateflow: StateFlow<UiState<T>>,
    content: @Composable (T) -> Unit,
) {
  val state by stateflow.collectAsState()

  when (state) {
    is UiState.Data<T> -> content((state as UiState.Data<T>).data)
    is UiState.Error -> Text((state as UiState.Error).reason)
    UiState.Loading -> Loader()
  }
}

sealed class UiState<out T> {
  object Loading : UiState<Nothing>()

  data class Error(
      val reason: String,
  ) : UiState<Nothing>()

  data class Data<T>(val data: T) : UiState<T>()
}

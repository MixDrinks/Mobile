package org.mixdrinks.ui.widgets.undomain

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import kotlinx.coroutines.flow.StateFlow
import org.mixdrinks.ui.widgets.Loader

@Composable
fun <T> ContentHolder(
    stateflow: StateFlow<UiState<T>>,
    content: @Composable (T) -> Unit,
) {
  val state by stateflow.collectAsState()

  when (val safeState = state) {
    is UiState.Data<T> -> {
      content(safeState.data)
    }
    is UiState.Error -> {
      Text(safeState.reason)
    }
    UiState.Loading -> {
      Loader()
    }
  }
}

@Immutable
sealed class UiState<out T> {
  @Immutable
  object Loading : UiState<Nothing>()

  @Immutable
  data class Error(
      val reason: String,
  ) : UiState<Nothing>()

  @Immutable
  data class Data<T>(val data: T) : UiState<T>()
}

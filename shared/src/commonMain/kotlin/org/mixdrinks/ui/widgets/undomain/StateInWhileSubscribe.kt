package org.mixdrinks.ui.widgets.undomain

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.stateIn

fun <T> Flow<UiState<T>>.stateInWhileSubscribe(): StateFlow<UiState<T>> {
  return this
      .distinctUntilChanged()
      .stateIn(
          CoroutineScope(Dispatchers.Main), SharingStarted.WhileSubscribed(STOP_SUBSCRIBE_TIME_OUT), UiState.Loading
      )
}

private const val STOP_SUBSCRIBE_TIME_OUT = 100L

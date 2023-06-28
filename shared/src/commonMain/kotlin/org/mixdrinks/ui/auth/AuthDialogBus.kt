package org.mixdrinks.ui.auth

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class AuthDialogBus {

    private val _showAuthDialog = MutableStateFlow(false)
    val showAuthDialog: StateFlow<Boolean> = _showAuthDialog

    fun tryEmit(value: Boolean) {
        _showAuthDialog.tryEmit(value)
    }

    suspend fun emit(value: Boolean) {
        _showAuthDialog.emit(value)
    }

}

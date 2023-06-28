package org.mixdrinks.ui.auth

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class AuthBus(
    private val tokenStorage: TokenStorage,
) {

    private val _showAuthDialog = MutableStateFlow(false)
    val showAuthDialog: StateFlow<Boolean> = _showAuthDialog

    private val logoutNotfier = mutableListOf<() -> Unit>()

    fun registerLogoutNotifier(notifier: () -> Unit) {
        logoutNotfier.add(notifier)
    }

    fun logout() {
        tokenStorage.clean()
        AuthCallbacks.logout()
        logoutNotfier.forEach { it() }
    }

    fun tryEmit(value: Boolean) {
        _showAuthDialog.tryEmit(value)
    }

    suspend fun emit(value: Boolean) {
        _showAuthDialog.emit(value)
    }

}

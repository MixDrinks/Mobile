package org.mixdrinks.ui.auth

import com.russhwolf.settings.Settings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class TokenStorage(
    private val settings: Settings,
) {

    private val _tokenFlow = MutableStateFlow(getToken())
    val tokenFlow: StateFlow<String?> = _tokenFlow

    fun setToken(token: String) {
        println("new token: $token")
        settings.putString(KEY_TOKEN, token)
        _tokenFlow.tryEmit(token)
    }

    fun getToken(): String? {
        return settings.getStringOrNull(KEY_TOKEN)
    }

    fun clean() {
        print("clean")
        settings.remove(KEY_TOKEN)
        _tokenFlow.tryEmit(null)
    }

    private companion object {
        const val KEY_TOKEN = "KEY_TOKEN"
    }

}

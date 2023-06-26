package org.mixdrinks.ui.auth

object AuthCallbacks {

    var appleAuthStart: () -> Unit = {}

    var googleAuthStart: () -> Unit = {}

    var emailAuthStart: (email: String, password: String) -> Unit = { _, _ -> }

    var logout: () -> Unit = {}

}

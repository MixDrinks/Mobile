import androidx.compose.ui.window.ComposeUIViewController
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import org.mixdrinks.app.MixDrinksApp
import org.mixdrinks.di.GraphHolder
import org.mixdrinks.ui.auth.AuthCallbacks

@Suppress("FunctionNaming")
fun MainViewController() = ComposeUIViewController {
    MixDrinksApp(DefaultComponentContext(LifecycleRegistry()), null)
}

@Suppress("FunctionNaming")
fun NewToken(token: String) {
    GraphHolder.graph.tokenStorage.setToken(token)
}

@Suppress("FunctionNaming")
fun setLogout(block: () -> Unit) {
    AuthCallbacks.logout = {
        block()
    }
}

fun setGoogleAuthStart(block: () -> Unit) {
    AuthCallbacks.googleAuthStart = {
        block()
    }
}

fun setAppleAuthStart(block: () -> Unit) {
    AuthCallbacks.appleAuthStart = {
        block()
    }
}

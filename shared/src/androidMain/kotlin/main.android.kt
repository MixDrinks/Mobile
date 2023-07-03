import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.decompose.defaultComponentContext
import com.arkivanov.essenty.backhandler.BackDispatcher
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import org.mixdrinks.app.MixDrinksApp
import org.mixdrinks.di.GraphHolder
import org.mixdrinks.ui.auth.AuthCallbacks

@Composable
fun MainView(deepLink : String?) {
    val context = (LocalContext.current as AppCompatActivity).defaultComponentContext()
    MixDrinksApp(context, deepLink)
}

@Suppress("FunctionNaming")
fun NewToken(token: String) {
    GraphHolder.graph.tokenStorage.setToken(token)
}

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

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.ComposeUIViewController
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.extensions.compose.jetbrains.PredictiveBackGestureIcon
import com.arkivanov.decompose.extensions.compose.jetbrains.PredictiveBackGestureOverlay
import com.arkivanov.essenty.backhandler.BackDispatcher
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import org.mixdrinks.app.MixDrinksApp
import org.mixdrinks.di.GraphHolder
import org.mixdrinks.ui.auth.AuthCallbacks
import platform.UIKit.UIViewController

@OptIn(ExperimentalDecomposeApi::class)
@Suppress("FunctionNaming")
fun MainViewController(): UIViewController {
    val backDispatcher = BackDispatcher()
    val componentContext =
        DefaultComponentContext(
            lifecycle = LifecycleRegistry(),
            backHandler = backDispatcher,
        )

    return ComposeUIViewController {
        PredictiveBackGestureOverlay(
            modifier = Modifier.fillMaxSize(),
            backDispatcher = backDispatcher,
            backIcon = { progress, _ ->
                PredictiveBackGestureIcon(
                    imageVector = Icons.Default.ArrowBack,
                    progress = progress,
                )
            }
        ) {
            MixDrinksApp(componentContext, null)
        }
    }
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

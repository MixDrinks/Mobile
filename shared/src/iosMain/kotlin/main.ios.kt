import androidx.compose.ui.window.ComposeUIViewController
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import org.mixdrinks.app.MixDrinksApp
import platform.UIKit.childViewControllerForScreenEdgesDeferringSystemGestures

@Suppress("FunctionNaming")
fun MainViewController() = ComposeUIViewController {
    MixDrinksApp(DefaultComponentContext(LifecycleRegistry()), null)
}

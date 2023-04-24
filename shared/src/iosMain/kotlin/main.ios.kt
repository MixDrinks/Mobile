import androidx.compose.ui.window.ComposeUIViewController
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import kotlin.reflect.KFunction0
import kotlinx.cinterop.COpaque
import kotlinx.cinterop.COpaquePointer
import org.mixdrinks.app.MixDrinksApp
import platform.UIKit.UIGestureRecognizer
import platform.UIKit.UIViewController
import platform.UIKit.inputViewController
import platform.UIKit.navigationController
import platform.darwin.NSObject

@Suppress("FunctionNaming")
fun MainViewController() = ComposeUIViewController {
  MixDrinksApp(DefaultComponentContext(LifecycleRegistry()))
}

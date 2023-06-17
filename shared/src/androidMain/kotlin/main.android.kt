import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.arkivanov.decompose.defaultComponentContext
import org.mixdrinks.app.MixDrinksApp

@Composable
fun MainView(deepLink : String?) {
    val context = (LocalContext.current as AppCompatActivity).defaultComponentContext()
    MixDrinksApp(context, deepLink)
}

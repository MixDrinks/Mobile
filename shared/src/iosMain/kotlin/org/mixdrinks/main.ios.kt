package org.mixdrinks

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Application
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import org.mixdrinks.styles.MixDrinksColors
import platform.UIKit.UIViewController

fun MainViewController(): UIViewController =
    Application("Example Application") {
      Column {
        Box(modifier = Modifier
            .background(MixDrinksColors.Main)
            .fillMaxWidth()
            .height(34.dp))
        val lifecycle = LifecycleRegistry()
        MixDrinksApp(DefaultComponentContext(lifecycle))
      }
    }

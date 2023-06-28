package org.mixdrinks.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arkivanov.decompose.extensions.compose.jetbrains.stack.Children
import com.arkivanov.decompose.extensions.compose.jetbrains.stack.animation.fade
import com.arkivanov.decompose.extensions.compose.jetbrains.stack.animation.stackAnimation
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource
import org.mixdrinks.app.styles.MixDrinksColors
import org.mixdrinks.ui.auth.AuthView
import org.mixdrinks.ui.main.MainContent
import org.mixdrinks.ui.profile.ProfileContent

@OptIn(ExperimentalResourceApi::class)
@Composable
internal fun RootContent(component: RootComponent, deepLink: String?) {
    val showAuthDialog by component.showAuthDialog.collectAsState()
    val tabs by component.selectedTab.collectAsState()

    Box {
        Scaffold(
            bottomBar = {
                BottomNavigation(
                    backgroundColor = MixDrinksColors.Main,
                    elevation = 4.dp
                ) {
                    tabs.forEach { tab ->
                        BottomNavigationItem(
                            icon = { Icon(painterResource(tab.tab.icon), contentDescription = tab.tab.title) },
                            label = { Text(text = tab.tab.title, fontSize = 12.sp) },
                            selectedContentColor = Color.White,
                            unselectedContentColor = Color.White.copy(alpha = 0.3f),
                            alwaysShowLabel = true,
                            selected = tab.isSelected,
                            onClick = {
                                component.open(tab.tab)
                            }
                        )
                    }
                }
            },
            content = {
                Children(
                    modifier = Modifier.padding(it),
                    stack = component.stack,
                    animation = stackAnimation(
                        animator = fade()
                    ),
                    content = {
                        when (val child = it.instance) {
                            is RootComponent.Child.Main -> MainContent(child.component, deepLink)
                            is RootComponent.Child.Profile -> ProfileContent(child.component)
                        }
                    }
                )
            }
        )

        if (showAuthDialog) {
            Box(modifier = Modifier
                .clickable(enabled = false, onClick = { })
                .fillMaxSize()
                .background(Color.DarkGray.copy(alpha = 0.5f))
            ) {
                AuthView(
                    modifier = Modifier
                        .padding(16.dp)
                        .align(Alignment.Center),
                    onClose = { component.authFlowCancel() }
                )
            }
        }
    }

    LaunchedEffect(deepLink) {
        if (deepLink != null) {
            component.open(RootComponent.BottomNavigationTab.Main)
        }
    }
}

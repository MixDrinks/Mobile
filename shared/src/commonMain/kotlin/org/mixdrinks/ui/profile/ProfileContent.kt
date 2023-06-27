package org.mixdrinks.ui.profile

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import org.mixdrinks.di.GraphHolder

@Composable
internal fun ProfileContent(component: ProfileComponent) {
    Text("Profile ${GraphHolder.graph.tokenStorage.getToken()}")
}

package org.mixdrinks.ui.profile

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import org.mixdrinks.ui.widgets.undomain.ContentHolder

@Composable
internal fun ProfileContent(component: ProfileComponent) {
    ContentHolder(
        stateflow = component.state,
    ) {
        Text("Profile $it")
    }
}

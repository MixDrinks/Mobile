package org.mixdrinks.ui.auth

import com.arkivanov.decompose.ComponentContext
import org.mixdrinks.ui.navigation.INavigator
import org.mixdrinks.ui.navigation.Navigator

internal class AuthComponent(
    private val componentContext: ComponentContext,
    private val navigator: Navigator,
) : ComponentContext by componentContext,
    INavigator by navigator {


}

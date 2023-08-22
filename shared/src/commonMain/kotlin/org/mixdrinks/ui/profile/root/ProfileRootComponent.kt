package org.mixdrinks.ui.profile.root

import com.arkivanov.decompose.ComponentContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.mixdrinks.ui.auth.AuthBus
import org.mixdrinks.ui.visited.authExecutor
import org.mixdrinks.ui.widgets.undomain.scope

internal class ProfileRootComponent(
    private val componentContext: ComponentContext,
    private val profileRootNavigation: ProfileRootNavigation,
    private val authBus: AuthBus,
    private val deleteAccountService: DeleteAccountService,
) : ComponentContext by componentContext,
    ProfileRootNavigation by profileRootNavigation {



    fun logout() {
        authBus.logout()
    }

    fun deleteAccount() {
        scope.launch {
            authExecutor {
                deleteAccountService.deleteAccount()
            }
        }.invokeOnCompletion {
            scope.launch(Dispatchers.Main) {
                authBus.logout()
            }
        }
    }

}

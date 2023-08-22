package org.mixdrinks.ui.profile.root

import de.jensklingenberg.ktorfit.http.DELETE

internal interface DeleteAccountService {

    @DELETE("/user-api/myself")
    suspend fun deleteAccount()

}

package org.mixdrinks.di

import com.russhwolf.settings.Settings
import de.jensklingenberg.ktorfit.Ktorfit
import io.ktor.client.HttpClient
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.mixdrinks.data.MixDrinksService
import org.mixdrinks.data.SnapshotRepository
import org.mixdrinks.ui.auth.AuthBus
import org.mixdrinks.ui.auth.TokenStorage
import org.mixdrinks.ui.list.main.MutableFilterStorage
import org.mixdrinks.ui.profile.root.DeleteAccountService
import org.mixdrinks.ui.visited.UserVisitedCocktailsService

internal class Graph {

    private val baseUrl = "https://api.mixdrinks.org/"

    init {
        GraphHolder.graph = this
    }

    private val settings: Settings = Settings()

    private val json = Json {
        isLenient = true
        ignoreUnknownKeys = true
    }

    private val snapshotService = Ktorfit.Builder()
        .httpClient(HttpClient {
            install(ContentNegotiation) {
                json(json)
            }
        })
        .baseUrl(baseUrl)
        .build()
        .create<MixDrinksService>()

    val tokenStorage = TokenStorage(settings)

    val httpClient =  HttpClient {
        install(Logging)
        install(ContentNegotiation) {
            json(json)
        }
        install(Auth) {
            bearer {
                loadTokens { BearerTokens(tokenStorage.getToken() ?: "", "") }
            }
        }
    }

    val visitedCocktailsService = Ktorfit.Builder()
        .httpClient(httpClient)
        .baseUrl(baseUrl)
        .build()
        .create<UserVisitedCocktailsService>()

    val deleteAccountService = Ktorfit.Builder()
        .httpClient(httpClient)
        .baseUrl(baseUrl)
        .build()
        .create<DeleteAccountService>()

    val snapshotRepository: SnapshotRepository = SnapshotRepository(snapshotService, settings, json)

    val mutableFilterStorage = MutableFilterStorage { snapshotRepository.get() }

    val authBus = AuthBus(tokenStorage)

}

internal object GraphHolder {
    lateinit var graph: Graph
}

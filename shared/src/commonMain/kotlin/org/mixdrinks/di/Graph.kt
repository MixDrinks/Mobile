package org.mixdrinks.di

import com.russhwolf.settings.Settings
import de.jensklingenberg.ktorfit.Ktorfit
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.mixdrinks.data.MixDrinksService
import org.mixdrinks.data.SnapshotRepository
import org.mixdrinks.ui.auth.TokenStorage
import org.mixdrinks.ui.list.main.MutableFilterStorage

internal class Graph {

    init {
        GraphHolder.graph = this
    }

    private val settings: Settings = Settings()

    private val json = Json {
        isLenient = true
        ignoreUnknownKeys = true
    }

    private val ktorfit = Ktorfit.Builder()
        .httpClient(HttpClient {
            install(ContentNegotiation) {
                json(json)
            }
        })
        .baseUrl("https://api.mixdrinks.org/")
        .build()
        .create<MixDrinksService>()

    val snapshotRepository: SnapshotRepository = SnapshotRepository(ktorfit, settings, json)

    val mutableFilterStorage = MutableFilterStorage { snapshotRepository.get() }

    val tokenStorage = TokenStorage(settings)

}

internal object GraphHolder {
    lateinit var graph: Graph
}

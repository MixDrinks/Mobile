package org.mixdrinks.data

import com.russhwolf.settings.Settings
import com.russhwolf.settings.set
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.mixdrinks.dto.SnapshotDto


internal class SnapshotRepository(
    private val mixDrinksService: MixDrinksService,
    private val settings: Settings,
    private val json: Json,
) {

    private val flowSnapshot: MutableStateFlow<SnapshotDto?> = MutableStateFlow(null)

    init {
        CoroutineScope(Dispatchers.Main).launch {
            settings.getStringOrNull(SNAPSHOT_KEY)?.let { cachedSnapshotStr ->
                try {
                    flowSnapshot.emit(json.decodeFromString(cachedSnapshotStr))
                } catch (_: Exception) {
                    updateSnapshot()
                }
            }

            val currentVersion = settings.getInt(SNAPSHOT_VERSION_KEY, -1)
            val newVersion = mixDrinksService.getVersion().versionCode
            if (currentVersion != newVersion) {
                updateSnapshot()
                settings[SNAPSHOT_VERSION_KEY] = newVersion
            }
        }
    }

    private suspend fun updateSnapshot() {
        try {
            println("Update snapshot")
            val snapshot = mixDrinksService.getSnapshot()
            settings[SNAPSHOT_KEY] = json.encodeToString(snapshot)
            flowSnapshot.emit(snapshot)
        } catch (_: Exception) {

        }
    }

    suspend fun get(): SnapshotDto {
        return flowSnapshot.filterNotNull().first()
    }

    companion object {
        private const val SNAPSHOT_KEY = "SNAPSHOT_KEY"
        private const val SNAPSHOT_VERSION_KEY = "SNAPSHOT_VERSION_KEY"
    }
}

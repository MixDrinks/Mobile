package org.mixdrinks.data

import com.russhwolf.settings.Settings
import com.russhwolf.settings.set
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.mixdrinks.app.utils.undomain.LazySuspend
import org.mixdrinks.dto.SnapshotDto


internal class SnapshotRepository(
    private val mixDrinksService: MixDrinksService,
    private val settings: Settings,
    private val json: Json,
) {

  private val snapshot = LazySuspend {
    val cacheValue = settings.getStringOrNull(SNAPSHOT_KEY)
    if (cacheValue != null) {
      json.decodeFromString(cacheValue)
    } else {
      val snapshot = mixDrinksService.getSnapshot()
      settings[SNAPSHOT_KEY] = json.encodeToString(snapshot)
      snapshot
    }
  }

  suspend fun get(): SnapshotDto {
    return snapshot()
  }

  companion object {
    private const val SNAPSHOT_KEY = "SNAPSHOT_KEY"
  }
}

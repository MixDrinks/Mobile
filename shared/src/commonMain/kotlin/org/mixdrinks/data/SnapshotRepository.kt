package org.mixdrinks.data

import org.mixdrinks.app.utils.undomain.LazySuspend
import org.mixdrinks.dto.SnapshotDto


class SnapshotRepository(private val mixDrinksService: MixDrinksService) {

  private val snapshot = LazySuspend { mixDrinksService.getSnapshot() }

  suspend fun get(): SnapshotDto {
    return snapshot()
  }
}

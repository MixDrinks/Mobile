package org.mixdrinks.cocktail.data

import org.mixdrinks.dto.SnapshotDto
import org.mixdrinks.utils.undomain.LazySuspend


class SnapshotRepository(private val mixDrinksService: MixDrinksService) {

  private val snapshot = LazySuspend { mixDrinksService.getSnapshot() }

  suspend fun get(): SnapshotDto {
    return snapshot()
  }
}

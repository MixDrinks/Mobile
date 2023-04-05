package org.mixdrinks.data

import de.jensklingenberg.ktorfit.http.GET
import org.mixdrinks.dto.SnapshotDto

internal interface MixDrinksService {

  @GET("/snapshot")
  suspend fun getSnapshot(): SnapshotDto

}

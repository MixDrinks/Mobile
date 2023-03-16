package org.mixdrinks.cocktail.data

import de.jensklingenberg.ktorfit.http.GET
import org.mixdrinks.dto.SnapshotDto

interface MixDrinksService {

  @GET("/snapshot")
  suspend fun getSnapshot(): SnapshotDto

}

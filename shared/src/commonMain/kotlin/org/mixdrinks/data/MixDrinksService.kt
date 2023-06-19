package org.mixdrinks.data

import de.jensklingenberg.ktorfit.http.GET
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.mixdrinks.dto.SnapshotDto

internal interface MixDrinksService {

    @GET("/snapshot")
    suspend fun getSnapshot(): SnapshotDto

    @GET("/version")
    suspend fun getVersion(): Version

}

@Serializable
data class Version(
    @SerialName("version_code")
    val versionCode: Int,
)

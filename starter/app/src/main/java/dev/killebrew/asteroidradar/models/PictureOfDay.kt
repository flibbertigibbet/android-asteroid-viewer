package dev.killebrew.asteroidradar.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import dev.killebrew.asteroidradar.database.DatabasePictureOfDay

@JsonClass(generateAdapter = true)
data class PictureOfDay(@Json(name = "media_type") val mediaType: String, val title: String,
                        val url: String)

fun PictureOfDay.asDatabaseModel(): DatabasePictureOfDay {
    return DatabasePictureOfDay(
        url = url,
        mediaType = mediaType,
        title = title
    )
}

fun PictureOfDay.asDomainModel(): PictureOfDay {
    return PictureOfDay(
        url = url,
        mediaType = mediaType,
        title = title
    )
}
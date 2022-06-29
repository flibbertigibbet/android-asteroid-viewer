package dev.killebrew.asteroidradar.api

import com.squareup.moshi.FromJson
import com.squareup.moshi.JsonQualifier
import com.squareup.moshi.JsonReader
import com.squareup.moshi.ToJson
import dev.killebrew.asteroidradar.models.Asteroid
import org.json.JSONArray
import org.json.JSONObject
import java.util.*

@Retention(AnnotationRetention.RUNTIME)
@JsonQualifier
annotation class AsteroidList

class AsteroidListAdapter {
    @FromJson
    @AsteroidList
    fun parseAsteroidList(reader: JsonReader): ArrayList<Asteroid> {
        val jsonArray = JSONArray()
        with(reader) {
            beginObject()
            while (hasNext()) {
                // read the values for the objects keyed by date (ignore the date key)
                nextName()
                val obj = readJsonValue()
                jsonArray.put(obj)
            }
            endObject()
        }

        return parseAsteroidsJsonResult(jsonArray)
    }

    // Unused, but required by moshi
    @ToJson
    fun asteroidListToJson(@AsteroidList asteroids: ArrayList<Asteroid>): JSONArray {
        return JSONArray()
    }
}

fun parseAsteroidsJsonResult(jsonArray: JSONArray): ArrayList<Asteroid> {
    val asteroidList = ArrayList<Asteroid>()

    // loop through date groupings
    for (i in 0 until jsonArray.length()) {
        @Suppress("UNCHECKED_CAST")
        val asteroidJsonList = (jsonArray.get(i) as ArrayList<Map<String, Any?>>)
        // loop through asteroids for the date
        asteroidJsonList.forEach {
            val asteroidJson = JSONObject(it)

            val id = asteroidJson.getLong("id")
            val codename = asteroidJson.getString("name")
            val absoluteMagnitude = asteroidJson.getDouble("absolute_magnitude_h")
            val estimatedDiameter = asteroidJson.getJSONObject("estimated_diameter")
                .getJSONObject("kilometers").getDouble("estimated_diameter_max")

            val closeApproachData = asteroidJson
                .getJSONArray("close_approach_data").getJSONObject(0)
            val closeApproachDate = closeApproachData.getString("close_approach_date")
            val relativeVelocity = closeApproachData.getJSONObject("relative_velocity")
                .getDouble("kilometers_per_second")
            val distanceFromEarth = closeApproachData.getJSONObject("miss_distance")
                .getDouble("astronomical")
            val isPotentiallyHazardous = asteroidJson
                .getBoolean("is_potentially_hazardous_asteroid")

            val asteroid = Asteroid(
                id,
                codename,
                closeApproachDate,
                absoluteMagnitude,
                estimatedDiameter,
                relativeVelocity,
                distanceFromEarth,
                isPotentiallyHazardous
            )
            asteroidList.add(asteroid)
        }
    }

    return asteroidList
}

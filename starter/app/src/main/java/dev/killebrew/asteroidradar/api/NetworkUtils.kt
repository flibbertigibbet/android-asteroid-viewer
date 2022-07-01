package dev.killebrew.asteroidradar.api

import com.squareup.moshi.FromJson
import com.squareup.moshi.JsonQualifier
import com.squareup.moshi.JsonReader
import com.squareup.moshi.ToJson
import dev.killebrew.asteroidradar.models.Asteroid
import dev.killebrew.asteroidradar.utils.Constants
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

@Retention(AnnotationRetention.RUNTIME)
@JsonQualifier
annotation class AsteroidList

class AsteroidListAdapter {

    private val apiDateFormat = SimpleDateFormat(Constants.API_QUERY_DATE_FORMAT, Locale.getDefault())
    private val isoDateFormat = SimpleDateFormat(Constants.ISO_DATE_FORMAT, Locale.getDefault())

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

    private fun parseAsteroidsJsonResult(jsonArray: JSONArray): ArrayList<Asteroid> {
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
                val closeApproachDate = apiDateToIso(
                    closeApproachData.getString("close_approach_date")
                )
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

    // Convert date string from API to ISO8601 format
    private fun apiDateToIso(apiDate: String): String {
        // default to today if date fails to parse
        val parsedDate = apiDateFormat.parse(apiDate) ?: Date()
        return isoDateFormat.format(parsedDate)
    }
}

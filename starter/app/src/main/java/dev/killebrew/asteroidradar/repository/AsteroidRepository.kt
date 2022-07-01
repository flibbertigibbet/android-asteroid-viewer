package dev.killebrew.asteroidradar.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import dev.killebrew.asteroidradar.database.AsteroidDatabase
import dev.killebrew.asteroidradar.database.asDomainModel
import dev.killebrew.asteroidradar.models.Asteroid
import dev.killebrew.asteroidradar.models.PictureOfDay
import dev.killebrew.asteroidradar.models.asDatabaseModel
import dev.killebrew.asteroidradar.network.Network
import dev.killebrew.asteroidradar.network.asDatabaseModel
import dev.killebrew.asteroidradar.utils.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

// based on:
// https://github.com/udacity/andfun-kotlin-dev-bytes/blob/master/app/src/main/java/com/example/android/devbyteviewer/repository/VideosRepository.kt
class AsteroidRepository(private val database: AsteroidDatabase, private val apiKey: String) {
    private val apiDateFormat = SimpleDateFormat(Constants.API_QUERY_DATE_FORMAT, Locale.getDefault())
    private val isoDateFormat = SimpleDateFormat(Constants.ISO_DATE_FORMAT, Locale.getDefault())
    private val today = isoDateFormat.format(Date())

    val asteroids: LiveData<List<Asteroid>> = Transformations.map(
        database.asteroidDao.getAsteroids()
    ) { it.asDomainModel() }

    val asteroidsForWeek: LiveData<List<Asteroid>> = Transformations.map(
        database.asteroidDao.getAsteroidsForWeek(today)
    ) { it.asDomainModel() }

    val asteroidsForDay: LiveData<List<Asteroid>> = Transformations.map(
        database.asteroidDao.getAsteroidsForDay(today)
    ) { it.asDomainModel() }

    val pictureOfDay: LiveData<PictureOfDay> = Transformations.map(
        database.asteroidDao.getPictureOfDay()
    ) { it?.asDomainModel() }


    suspend fun refreshAsteroids() {
        withContext(Dispatchers.IO) {
            // request all asteroids for the next week
            val calendar = Calendar.getInstance()
            calendar.time = Date()
            val currentDate = apiDateFormat.format(calendar.time)
            calendar.add(Calendar.DAY_OF_YEAR, Constants.DAYS_IN_WEEK)
            val nextWeek = apiDateFormat.format(calendar.time)

            val asteroidList = Network.asteroids.getAsteroidsAsync(
                apiKey, currentDate, nextWeek
            ).await()
            database.asteroidDao.clearAsteroids()
            database.asteroidDao.insertAsteroids(*asteroidList.asDatabaseModel())
        }
    }

    suspend fun refreshPictureOfDay() {
        withContext(Dispatchers.IO) {
            val pictureOfDay = Network.asteroids.getPictureOfDayAsync(apiKey).await()
            // ignore picture of the day if it is actually a video
            if (pictureOfDay.mediaType == Constants.IMAGE_MEDIA_TYPE) {
                database.asteroidDao.clearPicture()
                database.asteroidDao.insertPicture(pictureOfDay.asDatabaseModel())
            }
        }
    }
}

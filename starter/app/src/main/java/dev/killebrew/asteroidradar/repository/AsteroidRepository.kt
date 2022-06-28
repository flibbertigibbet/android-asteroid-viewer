package dev.killebrew.asteroidradar.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import dev.killebrew.asteroidradar.Asteroid
import dev.killebrew.asteroidradar.database.AsteroidDatabase
import dev.killebrew.asteroidradar.database.asDomainModel
import dev.killebrew.asteroidradar.network.Network
import dev.killebrew.asteroidradar.network.asDatabaseModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

// based on:
// https://github.com/udacity/andfun-kotlin-dev-bytes/blob/master/app/src/main/java/com/example/android/devbyteviewer/repository/VideosRepository.kt
class AsteroidRepository(private val database: AsteroidDatabase) {
    val asteroids: LiveData<List<Asteroid>> = Transformations.map(
        database.asteroidDao.getAsteroids()
    ) { it.asDomainModel() }


    suspend fun refreshAsteroids() {
        withContext(Dispatchers.IO) {
            val asteroidList = Network.asteroids.getAsteroids().await()
            database.asteroidDao.insertAll(*asteroidList.asDatabaseModel())
        }
    }
}

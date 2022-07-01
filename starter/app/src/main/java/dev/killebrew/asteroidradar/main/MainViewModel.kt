package dev.killebrew.asteroidradar.main

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import dev.killebrew.asteroidradar.R
import dev.killebrew.asteroidradar.database.getDatabase
import dev.killebrew.asteroidradar.repository.AsteroidRepository
import kotlinx.coroutines.launch

// based on:
// https://github.com/udacity/andfun-kotlin-dev-bytes/blob/master/app/src/main/java/com/example/android/devbyteviewer/viewmodels/DevByteViewModel.kt
class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val database = getDatabase(application)
    private val apiKey = application.getString(R.string.nasa_api_key)
    private val asteroidRepository = AsteroidRepository(database, apiKey)

    /* FIXME: Is this needed?
    init {
        viewModelScope.launch {
            // use daily cache if available, or call network if not
            if (asteroidRepository.asteroids.value.isNullOrEmpty()) {
                Log.d("ViewModel", "No asteroids in DB; refreshing ##################################")
                asteroidRepository.refreshAsteroids()
            }
            if (asteroidRepository.pictureOfDay.value == null) {
                asteroidRepository.refreshPictureOfDay()
            }
        }
    }
     */

    val asteroids = asteroidRepository.asteroids
    val pictureOfDay = asteroidRepository.pictureOfDay

    class Factory(val app: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return MainViewModel(app) as T
            }
            throw IllegalArgumentException("Unable to construct view model")
        }
    }
}
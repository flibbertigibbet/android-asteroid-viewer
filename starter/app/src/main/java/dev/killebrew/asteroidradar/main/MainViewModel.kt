package dev.killebrew.asteroidradar.main

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dev.killebrew.asteroidradar.R
import dev.killebrew.asteroidradar.database.getDatabase
import dev.killebrew.asteroidradar.repository.AsteroidRepository

// based on:
// https://github.com/udacity/andfun-kotlin-dev-bytes/blob/master/app/src/main/java/com/example/android/devbyteviewer/viewmodels/DevByteViewModel.kt
class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val database = getDatabase(application)
    private val apiKey = application.getString(R.string.nasa_api_key)
    private val asteroidRepository = AsteroidRepository(database, apiKey)

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
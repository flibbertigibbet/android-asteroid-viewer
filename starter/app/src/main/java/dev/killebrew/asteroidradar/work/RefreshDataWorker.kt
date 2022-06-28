package dev.killebrew.asteroidradar.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dev.killebrew.asteroidradar.database.getDatabase
import dev.killebrew.asteroidradar.repository.AsteroidRepository
import retrofit2.HttpException

// based on:
// https://github.com/udacity/andfun-kotlin-dev-bytes/blob/master/app/src/main/java/com/example/android/devbyteviewer/work/RefreshDataWork.kt
class RefreshDataWorker(appContext: Context, params: WorkerParameters):
    CoroutineWorker(appContext, params) {

    companion object {
        const val WORK_NAME = "RefreshDataWorker"
    }

    override suspend fun doWork(): Result {
        val database = getDatabase(applicationContext)
        val repository = AsteroidRepository(database)
        return try {
            repository.refreshAsteroids()
            Result.success()
        } catch (e: HttpException) {
            Result.retry()
        }
    }
}

package com.udacity.asteroidradar.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.udacity.asteroidradar.database.AsteroidDatabase
import com.udacity.asteroidradar.repository.AsteroidRadarRepository
import retrofit2.HttpException

class RefreshAsteroidsWorker (context: Context, parameters: WorkerParameters): CoroutineWorker(context, parameters) {

    companion object{
        const val WORK_NAME = "RefreshAsteroidsWorker"
    }

    override suspend fun doWork(): Result {
        val database = AsteroidDatabase.getInstance(applicationContext)
        val repository = AsteroidRadarRepository(database)

        return try {
            repository.refreshData()
            repository.deleteOldData()
            Result.success()
        } catch (exception: HttpException){
            Result.retry()
        }
    }
}
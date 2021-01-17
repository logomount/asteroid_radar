package com.udacity.asteroidradar.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.PictureOfDay
import com.udacity.asteroidradar.api.getAsteroids
import com.udacity.asteroidradar.api.getPictureOfDay
import com.udacity.asteroidradar.asDatabaseModel
import com.udacity.asteroidradar.database.AsteroidDatabase
import com.udacity.asteroidradar.database.asDomainModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.lang.Exception

enum class AsteroidFilter {WEEK_ASTEROIDS, TODAY_ASTEROIDS, SAVED_ASTEROIDS}

class AsteroidRadarRepository(private val database: AsteroidDatabase) {

    private val _selectedFilter = MutableLiveData<AsteroidFilter>()
    private val selectedFilter: LiveData<AsteroidFilter>
        get() = _selectedFilter

    val asteroids: LiveData<List<Asteroid>> = Transformations.switchMap(selectedFilter){filter ->
        when(filter){
            AsteroidFilter.WEEK_ASTEROIDS -> Transformations.map(database.asteroidDao.getWeekAsteroids()){it.asDomainModel()}
            AsteroidFilter.TODAY_ASTEROIDS -> Transformations.map(database.asteroidDao.getTodayAsteroids()){it.asDomainModel()}
            AsteroidFilter.SAVED_ASTEROIDS -> Transformations.map(database.asteroidDao.getSavedAsteroids()){it.asDomainModel()}
        }
    }

    private val _pictureOfDay = MutableLiveData<PictureOfDay>()
    val pictureOfDay: LiveData<PictureOfDay>
        get() = _pictureOfDay

    init {
        _selectedFilter.value = AsteroidFilter.WEEK_ASTEROIDS
    }

    fun updateFilter(asteroidFilter: AsteroidFilter){
        _selectedFilter.value = asteroidFilter
    }

    suspend fun refreshData(){
        withContext(Dispatchers.IO){
            try {
                val asteroids = getAsteroids()
                val pictureOfDay = getPictureOfDay()
                _pictureOfDay.postValue(pictureOfDay)
                database.asteroidDao.insertAll(asteroids.asDatabaseModel())
            } catch (e: Exception){
                Log.e("AsteroidRadarRepository", e.message!!)
            }
        }
    }

    suspend fun deleteOldData(){
        withContext(Dispatchers.IO){
            database.asteroidDao.deleteOldAsteroids()
        }
    }
}
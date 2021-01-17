package com.udacity.asteroidradar.main

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.udacity.asteroidradar.database.AsteroidDatabase
import com.udacity.asteroidradar.repository.AsteroidFilter
import com.udacity.asteroidradar.repository.AsteroidRadarRepository
import kotlinx.coroutines.launch

class MainViewModel(context: Context): ViewModel() {

    private val database = AsteroidDatabase.getInstance(context)
    val asteroidRepository = AsteroidRadarRepository(database)

    init {
        viewModelScope.launch {
            asteroidRepository.refreshData()
        }
    }

    fun updateFilter(asteroidFilter: AsteroidFilter){
        viewModelScope.launch {
            asteroidRepository.updateFilter(asteroidFilter)
        }
    }

    val asteroids = asteroidRepository.asteroids
    val pictureOfDay = asteroidRepository.pictureOfDay

}
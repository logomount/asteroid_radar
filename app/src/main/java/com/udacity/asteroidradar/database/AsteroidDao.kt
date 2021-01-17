package com.udacity.asteroidradar.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface AsteroidDao{

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(databaseAsteroids: List<DatabaseAsteroid>)

    @Query("SELECT * FROM asteroids_table WHERE closeApproachDate BETWEEN date('now') AND date('now','+7 days') ORDER BY closeApproachDate ASC")
    fun getWeekAsteroids(): LiveData<List<DatabaseAsteroid>>

    @Query("SELECT * FROM asteroids_table WHERE closeApproachDate = date('now')")
    fun getTodayAsteroids(): LiveData<List<DatabaseAsteroid>>

    @Query("SELECT * FROM asteroids_table ORDER BY closeApproachDate ASC")
    fun getSavedAsteroids(): LiveData<List<DatabaseAsteroid>>

    @Query("DELETE FROM asteroids_table WHERE closeApproachDate < date('now')")
    fun deleteOldAsteroids()

}
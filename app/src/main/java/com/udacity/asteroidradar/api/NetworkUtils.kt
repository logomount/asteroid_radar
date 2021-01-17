package com.udacity.asteroidradar.api

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.Constants
import com.udacity.asteroidradar.PictureOfDay
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Query
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

internal annotation class UseMoshiConverter
internal annotation class UseScalarisConverter

interface AsteroidsService {
    @UseScalarisConverter
    @GET("neo/rest/v1/feed?")
    suspend fun getAsteroids(
            @Query("start_date") startDate: String,
            @Query("end_date") endDate: String,
            @Query("api_key") apiKey: String
    ): String
}

interface PictureOfDayService {
    @UseMoshiConverter
    @GET("planetary/apod?")
    suspend fun getPictureOfDay(
            @Query("api_key") apiKey: String
    ): PictureOfDay
}

object Network{
    private val retrofit = Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(MultipleConverterFactory())
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .build()
    val asteroids = retrofit.create(AsteroidsService::class.java)
    val pictureOfDay = retrofit.create(PictureOfDayService::class.java)
}

suspend fun getAsteroids(): ArrayList<Asteroid> {
    val formattedDates = getNextSevenDaysFormattedDates()
    val asteroidsJsonResult = Network.asteroids.getAsteroids(formattedDates.get(0), formattedDates.get(formattedDates.size-1), Constants.API_KEY)
    return parseAsteroidsJsonResult(JSONObject(asteroidsJsonResult))
}

suspend fun getPictureOfDay(): PictureOfDay? {
    val pictureOfDay = Network.pictureOfDay.getPictureOfDay(Constants.API_KEY)
    return if (pictureOfDay.mediaType.equals("image")) {
        pictureOfDay
    } else { null }
}

fun parseAsteroidsJsonResult(jsonResult: JSONObject): ArrayList<Asteroid> {

    val nearEarthObjectsJson = jsonResult.getJSONObject("near_earth_objects")

    val asteroidList = ArrayList<Asteroid>()

    val nextSevenDaysFormattedDates = getNextSevenDaysFormattedDates()
    for (formattedDate in nextSevenDaysFormattedDates) {
        val dateAsteroidJsonArray = nearEarthObjectsJson.getJSONArray(formattedDate)

        for (i in 0 until dateAsteroidJsonArray.length()) {
            val asteroidJson = dateAsteroidJsonArray.getJSONObject(i)
            val id = asteroidJson.getLong("id")
            val codename = asteroidJson.getString("name")
            val absoluteMagnitude = asteroidJson.getDouble("absolute_magnitude_h")
            val estimatedDiameter = asteroidJson.getJSONObject("estimated_diameter")
                .getJSONObject("kilometers").getDouble("estimated_diameter_max")

            val closeApproachData = asteroidJson
                .getJSONArray("close_approach_data").getJSONObject(0)
            val relativeVelocity = closeApproachData.getJSONObject("relative_velocity")
                .getDouble("kilometers_per_second")
            val distanceFromEarth = closeApproachData.getJSONObject("miss_distance")
                .getDouble("astronomical")
            val isPotentiallyHazardous = asteroidJson
                .getBoolean("is_potentially_hazardous_asteroid")

            val asteroid = Asteroid(id, codename, formattedDate, absoluteMagnitude,
                estimatedDiameter, relativeVelocity, distanceFromEarth, isPotentiallyHazardous)
            asteroidList.add(asteroid)
        }
    }

    return asteroidList
}

private fun getNextSevenDaysFormattedDates(): ArrayList<String> {
    val formattedDateList = ArrayList<String>()

    val calendar = Calendar.getInstance()
    for (i in 0..Constants.DEFAULT_END_DATE_DAYS) {
        val currentTime = calendar.time
        val dateFormat = SimpleDateFormat(Constants.API_QUERY_DATE_FORMAT, Locale.getDefault())
        formattedDateList.add(dateFormat.format(currentTime))
        calendar.add(Calendar.DAY_OF_YEAR, 1)
    }

    return formattedDateList
}
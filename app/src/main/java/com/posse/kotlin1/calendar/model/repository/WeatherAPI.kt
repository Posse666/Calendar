package com.posse.kotlin1.calendar.model.repository

import com.posse.kotlin1.calendar.model.WeatherDTO
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherAPI {
    @GET("data/2.5/weather")
    fun getWeather(
        @Query("q") city: String,
        @Query("appid") apiKey: String,
        @Query("units") units: String
    ): Call<WeatherDTO>
}
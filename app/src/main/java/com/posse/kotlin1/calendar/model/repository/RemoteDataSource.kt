package com.posse.kotlin1.calendar.model.repository

import com.google.gson.GsonBuilder
import com.posse.kotlin1.calendar.model.WeatherDTO
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Callback
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

private const val WEATHER_API_KEY = "f3aa9070cc9d3224ac670f2e0f0a47c9"

class RemoteDataSource {
    private val weatherApi = Retrofit.Builder()
        .baseUrl("https://api.openweathermap.org/")
        .addConverterFactory(
            GsonConverterFactory.create(
                GsonBuilder().setLenient().create()
            )
        )
        .client(createOkHttpClient())
        .build().create(WeatherAPI::class.java)

    fun getWeatherDetails(city: String, callback: Callback<WeatherDTO>) {
        weatherApi.getWeather(city, WEATHER_API_KEY, "metric").enqueue(callback)
    }

    private fun createOkHttpClient(): OkHttpClient {
        val httpClient = OkHttpClient.Builder()
        httpClient.addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
        return httpClient.build()
    }
}
package com.posse.kotlin1.calendar.view.statistic

import android.app.IntentService
import android.content.Intent
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.gson.Gson
import com.posse.kotlin1.calendar.model.DETAILS_INTENT_FILTER
import com.posse.kotlin1.calendar.model.DETAILS_TEMP_EXTRA
import com.posse.kotlin1.calendar.model.WeatherDTO
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.MalformedURLException
import java.net.URL
import java.util.stream.Collectors
import javax.net.ssl.HttpsURLConnection

const val CITY_EXTRA = "City"
private const val REQUEST_GET = "GET"
private const val REQUEST_TIMEOUT = 10000
private const val WEATHER_API_KEY = "f3aa9070cc9d3224ac670f2e0f0a47c9"

class WeatherLoaderService(name: String = "WeatherLoaderService") : IntentService(name) {

    private val broadcastIntent = Intent(DETAILS_INTENT_FILTER)

    override fun onHandleIntent(intent: Intent?) {
        if (intent == null) {
            handleError(RuntimeException("Empty intent"))
        } else {
            val city = intent.getStringExtra(CITY_EXTRA)
            if (city == null) {
                handleError(RuntimeException("Empty data"))
            } else {
                loadWeather(city)
            }
        }
    }

    private fun loadWeather(city: String) {
        try {
            val uri =
                URL("https://api.openweathermap.org/data/2.5/weather?q=${city}&appid=${WEATHER_API_KEY}&units=metric")
            lateinit var urlConnection: HttpsURLConnection
            try {
                urlConnection = uri.openConnection() as HttpsURLConnection
                urlConnection.apply {
                    requestMethod = REQUEST_GET
                    readTimeout = REQUEST_TIMEOUT
                }
                val bufferedReader =
                    BufferedReader(InputStreamReader(urlConnection.inputStream))
                val weatherDTO: WeatherDTO =
                    Gson().fromJson(getLines(bufferedReader), WeatherDTO::class.java)
                onResponse(weatherDTO)
            } catch (e: Exception) {
                handleError(e)
            } finally {
                urlConnection.disconnect()
            }
        } catch (e: MalformedURLException) {
            handleError(e)
        }
    }

    private fun handleError(e: Exception) {
        Log.e("", "Fail connection", e)
        e.printStackTrace()
    }

    private fun getLines(reader: BufferedReader): String {
        return reader.lines().collect(Collectors.joining("\n"))
    }

    private fun onResponse(weatherDTO: WeatherDTO) {
        val main = weatherDTO.main
        if (main == null) {
            handleError(RuntimeException("Empty response"))
        } else {
            onSuccessResponse(main.temp?.toInt())
        }
    }

    private fun onSuccessResponse(temp: Int?) {
        broadcastIntent.putExtra(DETAILS_TEMP_EXTRA, temp)
        LocalBroadcastManager.getInstance(this).sendBroadcast(broadcastIntent)
    }
}
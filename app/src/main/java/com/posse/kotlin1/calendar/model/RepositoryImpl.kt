package com.posse.kotlin1.calendar.model

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.posse.kotlin1.calendar.view.statistic.WeatherLoader
import java.time.LocalDate

object RepositoryImpl : Repository {

    private val liveDataToObserve: MutableLiveData<Set<LocalDate>> = MutableLiveData()
    private val temperature: MutableLiveData<Int> = MutableLiveData()

    override fun removeLaterInitForTestingPurpose() {
        CalendarState.clearAll()
        var date = LocalDate.now()
        val daysNumber = (50 + Math.random() * 100).toInt()
        for (i in 1..daysNumber) {
            CalendarState.addDay(date)
            date = date.minusDays(1 + (Math.random() * 10).toLong())
        }
        liveDataToObserve.value = CalendarState.dates
    }

    override fun getLiveData(): LiveData<Set<LocalDate>> = liveDataToObserve

    override fun getDrankStateFromLocalStorage(): Set<LocalDate> {
        return CalendarState.dates
    }

    override fun changeState(date: LocalDate) {
        if (CalendarState.dates.contains(date)) {
            CalendarState.removeDay(date)
        } else CalendarState.addDay(date)
        liveDataToObserve.value = CalendarState.dates
    }

    override fun getTemperature(): LiveData<Int> = temperature

    override fun refreshTemperature(context: Context) {
        val onLoadListener: WeatherLoader.WeatherLoaderListener =
            object : WeatherLoader.WeatherLoaderListener {

                override fun onLoaded(weatherDTO: WeatherDTO) {
                    temperature.value = weatherDTO.main?.temp?.toInt()
                    OfflineData.getInstance(context).prefsData.temperature = temperature.value ?: 0
                }

                override fun onFailed(throwable: Throwable) {
                    Log.e("error", throwable.stackTrace.toString())
                }
            }
        val loader = WeatherLoader(onLoadListener, "Moscow")
        loader.loadWeather()
    }

    override fun getStartTemperature(context: Context): Int {
        return OfflineData.getInstance(context).prefsData.temperature
    }
}

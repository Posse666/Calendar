package com.posse.kotlin1.calendar.model.repository

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.posse.kotlin1.calendar.model.CalendarState
import com.posse.kotlin1.calendar.model.OfflineData
import com.posse.kotlin1.calendar.model.WeatherDTO
import com.posse.kotlin1.calendar.model.temperature
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDate

object RepositoryImpl : Repository {

    private val remoteDataSource: RemoteDataSource = RemoteDataSource()
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

    override fun refreshTemperature() {
        remoteDataSource.getWeatherDetails("Moscow", callBack)
    }

    override fun getStartTemperature(context: Context): Int? {
        return OfflineData.getInstance(context)?.prefsData?.temperature
    }

    private val callBack = object :
        Callback<WeatherDTO> {

        override fun onResponse(call: Call<WeatherDTO>, response: Response<WeatherDTO>) {
            val serverResponse: WeatherDTO? = response.body()
            if (response.isSuccessful && serverResponse != null) {
                temperature.value = serverResponse.main?.temp?.toInt()
                OfflineData.getInstance(null)?.prefsData?.temperature = temperature.value ?: 0
            }
        }

        override fun onFailure(call: Call<WeatherDTO>, t: Throwable) {
            Log.e("error", t.message.toString())
        }
    }
}

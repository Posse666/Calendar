package com.posse.kotlin1.calendar.model

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.posse.kotlin1.calendar.view.statistic.CITY_EXTRA
import com.posse.kotlin1.calendar.view.statistic.WeatherLoaderService
import java.time.LocalDate

const val DETAILS_INTENT_FILTER = "DETAILS INTENT FILTER"
const val DETAILS_RESPONSE_SUCCESS_EXTRA = "RESPONSE SUCCESS"
const val DETAILS_TEMP_EXTRA = "TEMPERATURE"
private const val TEMP_INVALID = -100

object RepositoryImpl : Repository {

    private val liveDataToObserve: MutableLiveData<Set<LocalDate>> = MutableLiveData()
    private val temperature: MutableLiveData<Int> = MutableLiveData()
    private val loadResultsReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            temperature.value = intent.getIntExtra(DETAILS_TEMP_EXTRA, TEMP_INVALID)
            OfflineData.getInstance(context).prefsData.temperature = temperature.value ?: 0
        }
    }


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
        context.let {
            it.startService(Intent(it, WeatherLoaderService::class.java).apply {
                putExtra(CITY_EXTRA, "Moscow")
            })
        }
        context.let {
            LocalBroadcastManager.getInstance(it)
                .registerReceiver(loadResultsReceiver, IntentFilter(DETAILS_INTENT_FILTER))
        }
    }

    override fun getStartTemperature(context: Context): Int {
        return OfflineData.getInstance(context).prefsData.temperature
    }
}

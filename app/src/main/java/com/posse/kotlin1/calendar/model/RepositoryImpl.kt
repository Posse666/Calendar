package com.posse.kotlin1.calendar.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import java.time.LocalDate

object RepositoryImpl : Repository {

    private val liveDataToObserve: MutableLiveData<Set<LocalDate>> = MutableLiveData()

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
}

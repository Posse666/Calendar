package com.posse.kotlin1.calendar.model.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.posse.kotlin1.calendar.app.App.Companion.getCalendarDao
import java.time.LocalDate

class RepositoryImpl : Repository {

    private val liveDataToObserve: MutableLiveData<Set<LocalDate>> = MutableLiveData()
    private val localRepositoryImpl: LocalRepositoryImpl = LocalRepositoryImpl(getCalendarDao())

    override fun removeLaterInitForTestingPurpose() {
//        CalendarState.clearAll()
        localRepositoryImpl.deleteAll()
        var date = LocalDate.now()
        val daysNumber = (50 + Math.random() * 100).toInt()
        for (i in 1..daysNumber) {
            localRepositoryImpl.saveEntity(date)
//            CalendarState.addDay(date)
            date = date.minusDays(1 + (Math.random() * 10).toLong())
        }
        liveDataToObserve.value = localRepositoryImpl.getAll()
    }

    override fun getLiveData(): LiveData<Set<LocalDate>> = liveDataToObserve

    override fun getDrankStateFromLocalStorage(): Set<LocalDate> {
        return localRepositoryImpl.getAll()
    }

    override fun changeState(date: LocalDate) {
//        if (CalendarState.dates.contains(date)) {
//            CalendarState.removeDay(date)
//        } else CalendarState.addDay(date)
        if (localRepositoryImpl.checkDate(date)) {
            localRepositoryImpl.deleteDate(date)
        } else localRepositoryImpl.saveEntity(date)
        liveDataToObserve.value = localRepositoryImpl.getAll()
    }
}

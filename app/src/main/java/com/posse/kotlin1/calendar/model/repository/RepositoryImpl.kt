package com.posse.kotlin1.calendar.model.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.posse.kotlin1.calendar.app.App.Companion.getCalendarDao
import com.posse.kotlin1.calendar.room.CalendarEntity
import java.time.LocalDate

object RepositoryImpl : Repository {

    private val liveDataToObserve: MutableLiveData<Set<LocalDate>> = MutableLiveData()
    private val localRepository: LocalRepository by lazy {
        LocalRepositoryImpl(
            getCalendarDao()
        )
    }

    override fun removeLaterInitForTestingPurpose() {
        Thread {
            localRepository.deleteAll()
            var date = LocalDate.now()
            val daysNumber = (50 + Math.random() * 100).toInt()
            for (i in 1..daysNumber) {
                localRepository.saveEntity(date)
                date = date.minusDays(1 + (Math.random() * 10).toLong())
            }
            liveDataToObserve.postValue(localRepository.getAll())
        }.start()
    }

    override fun getLiveData(): LiveData<Set<LocalDate>> = liveDataToObserve

    override fun changeState(date: LocalDate) {
        Thread {
            if (localRepository.checkDate(date)) {
                localRepository.deleteDate(date)
            } else localRepository.saveEntity(date)
            liveDataToObserve.postValue(localRepository.getAll())
        }.start()
    }

    override fun updateSate(date: LocalDate, longitude: Double, latitude: Double) {
        Thread {
            localRepository.updateEntity(date, longitude, latitude)
        }
    }

    override fun getLocation(date: LocalDate): CalendarEntity? {
        return localRepository.getEntity(date)
    }
}

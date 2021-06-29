package com.posse.kotlin1.calendar.model.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.posse.kotlin1.calendar.app.App.Companion.getCalendarDao
import java.time.LocalDate

object RepositoryImpl : Repository {

    private val liveDataToObserve: MutableLiveData<Set<LocalDate>> = MutableLiveData()
    private val localRepositoryImpl: LocalRepositoryImpl by lazy {LocalRepositoryImpl(getCalendarDao())}

    override fun removeLaterInitForTestingPurpose() {
        Thread {
            localRepositoryImpl.deleteAll()
            var date = LocalDate.now()
            val daysNumber = (50 + Math.random() * 100).toInt()
            for (i in 1..daysNumber) {
                localRepositoryImpl.saveEntity(date)
                date = date.minusDays(1 + (Math.random() * 10).toLong())
            }
            liveDataToObserve.postValue(localRepositoryImpl.getAll())
        }.start()
    }

    override fun getLiveData(): LiveData<Set<LocalDate>> = liveDataToObserve

    override fun changeState(date: LocalDate) {
        Thread {
            if (localRepositoryImpl.checkDate(date)) {
                localRepositoryImpl.deleteDate(date)
            } else localRepositoryImpl.saveEntity(date)
            liveDataToObserve.postValue(localRepositoryImpl.getAll())
        }.start()
    }
}

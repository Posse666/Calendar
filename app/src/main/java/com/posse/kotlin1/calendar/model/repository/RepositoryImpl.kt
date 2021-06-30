package com.posse.kotlin1.calendar.model.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import java.time.LocalDate

object RepositoryImpl : Repository {

    private val liveDataToObserve: MutableLiveData<Set<LocalDate>> = MutableLiveData()
    private val callback = RepositoryListener { liveDataToObserve.value = it }
    private val localRepository: LocalRepository = LocalRepositoryFirestoreImpl("", callback)

    override fun getLiveData(): LiveData<Set<LocalDate>> = liveDataToObserve

    override fun changeState(date: LocalDate) {
        if (localRepository.checkDate(date)) {
            localRepository.deleteDate(date)
        } else localRepository.saveDate(date)
    }
}

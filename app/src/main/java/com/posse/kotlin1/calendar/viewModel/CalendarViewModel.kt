package com.posse.kotlin1.calendar.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.posse.kotlin1.calendar.model.Repository
import com.posse.kotlin1.calendar.model.RepositoryImpl
import java.time.LocalDate

class CalendarViewModel(
    private val mLiveDataToObserve: MutableLiveData<Set<LocalDate>> = MutableLiveData(),
    private val mRepository: Repository = RepositoryImpl()
) : ViewModel() {

    fun getLiveData() = mLiveDataToObserve

    fun refreshDrankState() = getDataFromLocalSource()

    fun dayClicked(date: LocalDate) {
        mRepository.changeState(date)
//        mLiveDataToObserve.value = mRepository.getDrankStateFromLocalStorage()
    }

    private fun getDataFromLocalSource() {
        mRepository.init()
        mLiveDataToObserve.value = mRepository.getDrankStateFromLocalStorage()
    }
}
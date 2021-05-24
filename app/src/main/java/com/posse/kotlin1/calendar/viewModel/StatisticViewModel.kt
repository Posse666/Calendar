package com.posse.kotlin1.calendar.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.posse.kotlin1.calendar.model.Repository
import com.posse.kotlin1.calendar.model.RepositoryImpl
import java.time.LocalDate

class StatisticViewModel(
    private val liveDataToObserve: MutableLiveData<Set<LocalDate>> = MutableLiveData(),
    private val repository: Repository = RepositoryImpl()
) : ViewModel() {

    fun getLiveData() = liveDataToObserve

    fun refreshData() = getDataFromLocalSource()

    fun getDrankDaysQuantity() = repository.getDrinkDaysInThisYear()

    fun getThisYearDaysQuantity() = repository.getThisYearDaysQuantity()

    private fun getDataFromLocalSource() {
        liveDataToObserve.value = repository.getDrankStateFromLocalStorage()
        // change to observe repo liveData
    }
}
package com.posse.kotlin1.calendar.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.posse.kotlin1.calendar.model.CalendarState
import com.posse.kotlin1.calendar.model.Repository
import com.posse.kotlin1.calendar.model.RepositoryImpl

class CalendarViewModel(
    private val liveDataToObserve: MutableLiveData<CalendarState> = MutableLiveData(),
    private val repository: Repository = RepositoryImpl()
) : ViewModel() {

    fun getLiveData() = liveDataToObserve

    fun getWeatherFromLocalSource() = getDataFromLocalSource()

    fun getWeatherFromRemoteSource() = getDataFromLocalSource()

    private fun getDataFromLocalSource() {
        liveDataToObserve.value = CalendarState().Loading
        Thread {
            Thread.sleep(1000)
            liveDataToObserve.postValue(CalendarState().Success(repository.getWeatherFromLocalStorage()))
        }.start()
    }
}
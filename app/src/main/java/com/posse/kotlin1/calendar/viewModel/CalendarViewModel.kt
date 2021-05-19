package com.posse.kotlin1.calendar.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.posse.kotlin1.calendar.model.Repository
import com.posse.kotlin1.calendar.model.RepositoryImpl
import java.time.LocalDate

class CalendarViewModel(
    private val liveDataToObserve: MutableLiveData<Map<LocalDate, Boolean>> = MutableLiveData(),
    private val repository: Repository = RepositoryImpl()
) : ViewModel() {

    fun getLiveData() = liveDataToObserve

    fun refreshDrankState() = getDataFromLocalSource()

    fun dayClicked(date: LocalDate){
        repository.changeState(date)
//        liveDataToObserve.value = repository.getDrankStateFromLocalStorage()
    }

    private fun getDataFromLocalSource() {
//        liveDataToObserve.value = CalendarState().Loading
        repository.init()
        liveDataToObserve.value = repository.getDrankStateFromLocalStorage()
//        Thread {
//            Thread.sleep(1000)
//            liveDataToObserve.postValue(CalendarState().Success(repository.getWeatherFromLocalStorage()))
//        }.start()
    }
}
package com.posse.kotlin1.calendar.viewModel

import java.time.LocalDate

class CalendarViewModel : BaseStatsViewModel() {

    fun dayClicked(date: LocalDate) {
        repository.changeState(date)
//        mLiveDataToObserve.value = mRepository.getDrankStateFromLocalStorage()
    }
}
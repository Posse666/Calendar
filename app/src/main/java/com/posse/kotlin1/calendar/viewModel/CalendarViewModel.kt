package com.posse.kotlin1.calendar.viewModel

import com.posse.kotlin1.calendar.room.CalendarEntity
import java.time.LocalDate

class CalendarViewModel : BaseStatsViewModel() {

    fun dayClicked(date: LocalDate) = repository.changeState(date)

    fun setLocation(date: LocalDate, longitude: Double = 0.0, latitude: Double = 0.0) =
        repository.updateSate(date, longitude, latitude)

    fun getLocation(date: LocalDate, callback: (CalendarEntity?) -> Any?) {
        repository.getLocation(date, callback)
    }
}
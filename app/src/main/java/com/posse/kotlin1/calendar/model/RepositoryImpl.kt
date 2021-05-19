package com.posse.kotlin1.calendar.model

import java.time.LocalDate

class RepositoryImpl : Repository {
    override fun init() {
        CalendarState.clearAll()
        var date = LocalDate.now()
        val daysNumber = (10 + Math.random() * 10).toInt()
        for (i in 1..daysNumber) {
            CalendarState.addDay(date, true)
            date = date.minusDays(1 + (Math.random() * 10).toLong())
        }
    }

    override fun getDrankStateFromLocalStorage(): Map<LocalDate, Boolean> {
        return CalendarState.dates
    }

    override fun getState(date: LocalDate): Boolean? {
        return CalendarState.dates[date]
    }

    override fun changeState(date: LocalDate) {
        if (CalendarState.dates[date] == true){
            CalendarState.removeDay(date)
        } else CalendarState.addDay(date,true)
    }

}

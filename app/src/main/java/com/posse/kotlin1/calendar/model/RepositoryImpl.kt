package com.posse.kotlin1.calendar.model

import java.time.LocalDate

class RepositoryImpl : Repository {
    override fun init() {
        CalendarState.clearAll()
        var date = LocalDate.now()
        val daysNumber = (50 + Math.random() * 100).toInt()
        for (i in 1..daysNumber) {
            CalendarState.addDay(date)
            date = date.minusDays(1 + (Math.random() * 10).toLong())
        }
    }

    override fun getDrankStateFromLocalStorage(): Set<LocalDate> {
        return CalendarState.dates
    }

    override fun changeState(date: LocalDate) {
        if (CalendarState.dates.contains(date)) {
            CalendarState.removeDay(date)
        } else CalendarState.addDay(date)
    }

}

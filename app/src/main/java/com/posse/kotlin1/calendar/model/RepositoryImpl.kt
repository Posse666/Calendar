package com.posse.kotlin1.calendar.model

import java.time.LocalDate
import java.time.Year
import java.time.temporal.ChronoUnit

class RepositoryImpl : Repository {

    init {
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

    override fun getDrinkDaysInThisYear(): Int {
        var result = 0
        val currentYear: LocalDate = LocalDate.ofYearDay(Year.now().value, 1)
        CalendarState.dates.forEach {
            if (it.isAfter(currentYear) || it.isEqual(currentYear)) result++
        }
        return result
    }

    override fun getThisYearDaysQuantity(): Int {
        return (ChronoUnit.DAYS.between(
            LocalDate.ofYearDay(Year.now().value, 1),
            LocalDate.now()
        ) + 1).toInt()
    }
}

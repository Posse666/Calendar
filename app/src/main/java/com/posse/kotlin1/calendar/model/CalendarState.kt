package com.posse.kotlin1.calendar.model

import java.time.LocalDate

object CalendarState {
    private val drinkDates: MutableSet<LocalDate> = mutableSetOf()
    val dates: Set<LocalDate>
        get() = drinkDates

    fun addDay(date: LocalDate) {
        drinkDates.add(date)
    }

    fun removeDay(date: LocalDate) {
        drinkDates.remove(date)
    }

    fun clearAll() {
        drinkDates.clear()
    }
}
package com.posse.kotlin1.calendar.model

import java.time.LocalDate
import java.util.*
import kotlin.collections.HashMap

object CalendarState {
    private val drinkDates: MutableMap<LocalDate, Boolean> = HashMap()
    val dates: Map<LocalDate, Boolean>
        get() = Collections.unmodifiableMap(drinkDates)

    fun addDay(date: LocalDate, state: Boolean) {
        drinkDates[date] = state
    }

    fun removeDay(date: LocalDate) {
        drinkDates.remove(date)
    }

    fun clearAll() {
        drinkDates.clear()
    }
}
package com.posse.kotlin1.calendar.model

import java.time.LocalDate
import java.util.*

object CalendarState {
    private val mDrinkDates: MutableSet<LocalDate> = mutableSetOf()
    val dates: Set<LocalDate>
        get() = Collections.unmodifiableSet(mDrinkDates)

    fun addDay(date: LocalDate) {
        mDrinkDates.add(date)
    }

    fun removeDay(date: LocalDate) {
        mDrinkDates.remove(date)
    }

    fun clearAll() {
        mDrinkDates.clear()
    }
}
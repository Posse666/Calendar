package com.posse.kotlin1.calendar.utils

import com.posse.kotlin1.calendar.room.CalendarEntity
import java.time.LocalDate

fun convertEntityToLocalDate(entityList: List<CalendarEntity>): List<LocalDate> {
    return entityList.map {
        LocalDate.ofEpochDay(it.date)
    }
}

fun convertLocalDateToEntity(date: LocalDate): CalendarEntity {
    return CalendarEntity(0, date.toEpochDay())
}

fun convertLocalDateToLong(date: LocalDate): Long {
    return date.toEpochDay()
}
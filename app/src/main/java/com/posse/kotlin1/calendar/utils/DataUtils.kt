package com.posse.kotlin1.calendar.utils

import java.time.LocalDate

fun convertLocalDateToLong(date: LocalDate): Long {
    return date.toEpochDay()
}

fun convertLongToLocalDale(date: Long): LocalDate {
    return LocalDate.ofEpochDay(date)
}
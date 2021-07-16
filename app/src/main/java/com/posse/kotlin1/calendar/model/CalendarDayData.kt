package com.posse.kotlin1.calendar.model

const val DATE = "Date"

data class CalendarDayData(
    val id: String = "",
    var email: String = "",
    val date: Long = 0
)
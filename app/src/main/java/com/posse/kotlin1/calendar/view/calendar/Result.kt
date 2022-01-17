package com.posse.kotlin1.calendar.view.calendar

import java.time.LocalDate

sealed class Result {
    data class Success(val holidays: MutableSet<LocalDate>?) : Result()
    data class Offline(val holidays: MutableSet<LocalDate>?) : Result()
    object Error : Result()
}
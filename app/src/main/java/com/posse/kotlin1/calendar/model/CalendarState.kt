package com.posse.kotlin1.calendar.model

sealed class CalendarState {
    data class Success(val weatherData: Any) : CalendarState()
    data class Error(val error: Throwable) : CalendarState()
    object Loading : CalendarState()
}

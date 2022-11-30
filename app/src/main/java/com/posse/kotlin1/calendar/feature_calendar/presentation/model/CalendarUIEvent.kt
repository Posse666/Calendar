package com.posse.kotlin1.calendar.feature_calendar.presentation.model

sealed class CalendarUIEvent {
    object ErrorLoading : CalendarUIEvent()
}
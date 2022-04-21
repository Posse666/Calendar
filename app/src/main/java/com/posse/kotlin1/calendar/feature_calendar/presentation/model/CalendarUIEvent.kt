package com.posse.kotlin1.calendar.feature_calendar.presentation.model

import java.time.LocalDate

sealed class CalendarUIEvent {
    object ErrorLoading : CalendarUIEvent()
    data class ScrollToSelectedStatistic(val stats: List<LocalDate>) : CalendarUIEvent()
}
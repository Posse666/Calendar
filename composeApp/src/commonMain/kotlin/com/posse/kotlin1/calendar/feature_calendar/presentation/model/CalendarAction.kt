package com.posse.kotlin1.calendar.feature_calendar.presentation.model

sealed interface CalendarAction {
    data object ErrorLoading : CalendarAction
    data class ScrollToIndex(val index: Int, val animate: Boolean = false) : CalendarAction
}
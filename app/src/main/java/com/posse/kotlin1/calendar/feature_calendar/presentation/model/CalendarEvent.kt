package com.posse.kotlin1.calendar.feature_calendar.presentation.model

import com.posse.kotlin1.calendar.feature_calendar.domain.model.DayData

sealed class CalendarEvent {
    data class DateClicked(val day: DayData): CalendarEvent()
    data class ToggleStatistic(val isExpanded: Boolean): CalendarEvent()
    data class StatisticClicked(val statisticEntry: StatisticEntry) : CalendarEvent()
}

enum class StatisticEntry{
    DaysOverall,
    DrunkRowThisYear,
    DrunkRowOverall,
    FreshRowThisYear,
    FreshRowOverall
}
package com.posse.kotlin1.calendar.feature_calendar.presentation.model

import com.posse.kotlin1.calendar.feature_calendar.domain.model.MonthData

data class CalendarState(
    val isLoading: Boolean = false,
    val isStatsEverShown: Boolean = false,
    val isStatsExpanded: Boolean = false,
    val isMyCalendar: Boolean = false,
    val calendarData: List<MonthData> = emptyList(),
    val statistic: StatisticWithDaysState = StatisticWithDaysState()
)
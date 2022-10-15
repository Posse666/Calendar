package com.posse.kotlin1.calendar.feature_calendar.presentation.model

import com.posse.kotlin1.calendar.feature_calendar.domain.model.MonthData

data class CalendarState(
    val isLoading: Boolean = false,
    val isStatisticOpened: Boolean = false,
    val isStatsEverShown: Boolean = false,
    val calendarData: List<MonthData> = emptyList(),
    val statistic: StatisticState = StatisticState()
)

data class StatisticState(
    val totalDaysThisYear: Int = 0,
    val drinkRowThisYear: Int = 0,
    val drinkRowAllTime: Int = 0,
    val freshRowThisYear: Int = 0,
    val freshRowAllTime: Int = 0
)
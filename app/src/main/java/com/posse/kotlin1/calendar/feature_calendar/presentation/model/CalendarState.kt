package com.posse.kotlin1.calendar.feature_calendar.presentation.model

import com.posse.kotlin1.calendar.feature_calendar.domain.model.DayData

data class CalendarState(
    val isFriendsSectionVisible: Boolean = false,
    val selectedFriend: String? = null,
    val isLoading: Boolean = false,
    val dates: Set<DayData> = emptySet(),
    val isStatisticOpened: Boolean = false,
    val statistic: StatisticState = StatisticState()
)

data class StatisticState(
    val totalDaysThisYear: Int = 0,
    val drinkRowThisYear: Int = 0,
    val drinkRowAllTime: Int = 0,
    val freshRowThisYear: Int = 0,
    val freshRowAllTime: Int = 0
)
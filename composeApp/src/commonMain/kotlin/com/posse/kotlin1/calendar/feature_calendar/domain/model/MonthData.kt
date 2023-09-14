package com.posse.kotlin1.calendar.feature_calendar.domain.model

import kotlinx.datetime.Month

data class MonthData(
    val month: Month,
    val year: Int,
    val weeks: List<List<DayData?>>
)
package com.posse.kotlin1.calendar.feature_calendar.domain.model

import java.time.YearMonth

data class MonthData(
    val yearMonth: YearMonth,
    val weeks: List<List<DayData?>>
)

package com.posse.kotlin1.calendar.feature_calendar.presentation.model

import java.time.LocalDate

data class StatisticWithDaysState(
    val daysOverall: List<LocalDate> = emptyList(),
    val drunkRowThisYear: List<LocalDate> = emptyList(),
    val drunkRowOverall: List<LocalDate> = emptyList(),
    val freshRowThisYear: List<LocalDate> = emptyList(),
    val freshRowOverall: List<LocalDate> = emptyList()
)

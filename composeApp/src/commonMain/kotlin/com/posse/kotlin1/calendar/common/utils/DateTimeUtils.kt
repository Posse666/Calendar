package com.posse.kotlin1.calendar.common.utils

import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn

object DateTimeUtils {
    val today: LocalDate
        get() = Clock.System.todayIn(TimeZone.currentSystemDefault())
}
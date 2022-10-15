package com.posse.kotlin1.calendar.feature_calendar.domain.model

import com.posse.kotlin1.calendar.common.domain.model.DrinkType
import java.time.LocalDate

data class DayData(val date: LocalDate, val drinkType: DrinkType? = null) {
    override fun toString(): String = date.toString()
}
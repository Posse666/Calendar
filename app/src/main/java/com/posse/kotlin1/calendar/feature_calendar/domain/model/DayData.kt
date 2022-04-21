package com.posse.kotlin1.calendar.feature_calendar.domain.model

import com.posse.kotlin1.calendar.common.data.utils.convertLongToLocalDale

data class DayData(val date: Long, val drinkType: String?) {
    override fun toString(): String = convertLongToLocalDale(date).toString()
}
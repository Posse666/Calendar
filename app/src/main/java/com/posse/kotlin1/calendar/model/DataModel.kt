package com.posse.kotlin1.calendar.model

import com.posse.kotlin1.calendar.utils.convertLongToLocalDale

data class DataModel(val date: Long, val drinkType: String?) {
    override fun toString(): String = convertLongToLocalDale(date).toString()
}
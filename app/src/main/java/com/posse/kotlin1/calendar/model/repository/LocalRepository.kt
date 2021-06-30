package com.posse.kotlin1.calendar.model.repository

import com.posse.kotlin1.calendar.model.CalendarDayData
import java.time.LocalDate

interface LocalRepository {

    fun saveDate(date: LocalDate)
    fun deleteDate(date: LocalDate)
    fun deleteAll()
    fun checkDate(date: LocalDate): Boolean
    fun getDate(date: LocalDate): CalendarDayData?
}

fun interface RepositoryListener {
    fun onFetchComplete(data: Set<LocalDate>)
}
package com.posse.kotlin1.calendar.model.repository

import androidx.lifecycle.LiveData
import com.posse.kotlin1.calendar.model.CalendarDayData
import java.time.LocalDate
import java.util.HashMap

interface Repository {

    fun getLiveData(): LiveData<HashMap<LocalDate, CalendarDayData>>

    fun changeState(date: LocalDate)
}
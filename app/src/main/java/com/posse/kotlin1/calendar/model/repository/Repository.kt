package com.posse.kotlin1.calendar.model.repository

import androidx.lifecycle.LiveData
import com.posse.kotlin1.calendar.room.CalendarEntity
import java.time.LocalDate

interface Repository {

    fun removeLaterInitForTestingPurpose()

    fun getLiveData(): LiveData<Set<LocalDate>>

    fun changeState(date: LocalDate)

    fun updateSate(date: LocalDate, longitude: Double, latitude: Double)

    fun getLocation(date: LocalDate, callback: (CalendarEntity?) -> Any?)
}
package com.posse.kotlin1.calendar.model

import android.content.Context
import androidx.lifecycle.LiveData
import java.time.LocalDate

interface Repository {

    fun removeLaterInitForTestingPurpose()

    fun getLiveData(): LiveData<Set<LocalDate>>

    fun getDrankStateFromLocalStorage(): Set<LocalDate>

    fun changeState(date: LocalDate)

    fun getTemperature(): LiveData<Int>

    fun refreshTemperature(context: Context)

    fun getStartTemperature(context: Context): Int
}
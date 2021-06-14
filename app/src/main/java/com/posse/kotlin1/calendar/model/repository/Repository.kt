package com.posse.kotlin1.calendar.model.repository

import androidx.lifecycle.LiveData
import java.time.LocalDate

interface Repository {

    fun removeLaterInitForTestingPurpose()

    fun getLiveData(): LiveData<Set<LocalDate>>

    fun changeState(date: LocalDate)
}
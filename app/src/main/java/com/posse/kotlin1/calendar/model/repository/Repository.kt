package com.posse.kotlin1.calendar.model.repository

import androidx.lifecycle.LiveData
import java.time.LocalDate

interface Repository : BaseRepo {

    fun mergeData(newMail: String)

    fun getLiveData(): LiveData<HashSet<LocalDate>>

    fun changeState(date: LocalDate)
}
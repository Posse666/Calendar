package com.posse.kotlin1.calendar.model

import java.time.LocalDate

interface Repository {

    fun init()

    fun getDrankStateFromLocalStorage(): Map<LocalDate, Boolean>

    fun getState (date: LocalDate): Boolean?

    fun changeState (date: LocalDate)
}
package com.posse.kotlin1.calendar.model

import java.time.LocalDate

interface Repository {

    fun getDrankStateFromLocalStorage(): Set<LocalDate>

    fun changeState(date: LocalDate)

    fun getDrinkDaysInThisYear(): Int

    fun getThisYearDaysQuantity(): Int
}
package com.posse.kotlin1.calendar.model.repository

import java.time.LocalDate

interface LocalRepository {
    fun getAll(): Set<LocalDate>
    fun saveEntity(date: LocalDate)
    fun deleteDate(date: LocalDate)
    fun deleteAll()
    fun checkDate(date: LocalDate): Boolean
}
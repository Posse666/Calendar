package com.posse.kotlin1.calendar.model.repository

import com.posse.kotlin1.calendar.room.CalendarDao
import com.posse.kotlin1.calendar.utils.convertEntityToLocalDate
import com.posse.kotlin1.calendar.utils.convertLocalDateToEntity
import com.posse.kotlin1.calendar.utils.convertLocalDateToLong
import java.time.LocalDate

class LocalRepositoryImpl(private val localDataSource: CalendarDao) : LocalRepository {
    override fun getAll(): Set<LocalDate> {
        return convertEntityToLocalDate(localDataSource.all()).toSet()
    }

    override fun saveEntity(date: LocalDate) {
        localDataSource.insert(convertLocalDateToEntity(date))
    }

    override fun deleteDate(date: LocalDate) {
        localDataSource.deleteByDate(convertLocalDateToLong(date))
    }

    override fun deleteAll() {
        localDataSource.deleteAll()
    }

    override fun checkDate(date: LocalDate): Boolean {
        return localDataSource.getDataByDate(convertLocalDateToLong(date))?.date != null
    }
}
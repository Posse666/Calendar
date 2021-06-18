package com.posse.kotlin1.calendar.room

import androidx.room.*

@Dao
interface CalendarDao {

    @Query("SELECT * FROM CalendarEntity")
    fun all(): List<CalendarEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(entity: CalendarEntity)

    @Query("SELECT * FROM CalendarEntity WHERE date LIKE :date")
    fun getDataByDate(date: Long): CalendarEntity?

    @Update
    fun update(entity: CalendarEntity)

    @Delete
    fun delete(entity: CalendarEntity)

    @Query("DELETE FROM CalendarEntity WHERE date = :date")
    fun deleteByDate(date: Long)

    @Query("DELETE FROM CalendarEntity")
    fun deleteAll()
}

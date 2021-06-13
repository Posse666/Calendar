package com.posse.kotlin1.calendar.room

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [CalendarEntity::class], version = 1, exportSchema = false)
abstract class CalendarDataBase : RoomDatabase() {

    abstract fun calendarDao(): CalendarDao
}
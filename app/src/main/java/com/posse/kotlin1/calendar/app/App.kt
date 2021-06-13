package com.posse.kotlin1.calendar.app

import android.app.Application
import androidx.room.Room
import com.posse.kotlin1.calendar.room.CalendarDao
import com.posse.kotlin1.calendar.room.CalendarDataBase

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        appInstance = this
    }

    companion object {

        private var appInstance: App? = null
        private var db: CalendarDataBase? = null
        private const val DB_NAME = "Calendar.db"

        fun getCalendarDao(): CalendarDao {
            if (db == null) {
                synchronized(CalendarDataBase::class.java) {
                    if (db == null) {
                        if (appInstance == null) throw IllegalStateException("Application is null while creating DataBase")
                        db = Room.databaseBuilder(
                            appInstance!!.applicationContext,
                            CalendarDataBase::class.java,
                            DB_NAME
                        )
                            .allowMainThreadQueries()
                            .build()
                    }
                }
            }
            return db!!.calendarDao()
        }
    }
}
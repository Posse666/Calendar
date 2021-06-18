package com.posse.kotlin1.calendar.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class CalendarEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val date: Long = 0,
    var longitude: Double = 0.0,
    var latitude: Double = 0.0
)

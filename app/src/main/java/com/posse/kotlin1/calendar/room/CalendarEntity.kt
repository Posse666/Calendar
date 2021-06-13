package com.posse.kotlin1.calendar.room

import androidx.room.Entity
import androidx.room.PrimaryKey

const val ID = "id"
const val DATE = "date"

@Entity
data class CalendarEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val date: Long = 0,
)

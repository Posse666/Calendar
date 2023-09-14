package com.posse.kotlin1.calendar.common.domain.repository

import com.posse.kotlin1.calendar.feature_calendar.domain.model.DayData

interface MessageHandler {
    suspend fun sendMessage(userMail: String, day: DayData)
}
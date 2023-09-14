package com.posse.kotlin1.calendar.feature_calendar.domain.use_cases

import com.posse.kotlin1.calendar.common.domain.repository.MessageHandler
import com.posse.kotlin1.calendar.feature_calendar.domain.model.DayData

class SendMessage (
    private val messageHandler: MessageHandler
) {
    suspend operator fun invoke(mail: String, day: DayData) {
        messageHandler.sendMessage(mail, day)
    }
}
package com.posse.kotlin1.calendar.feature_calendar.domain.use_case

import com.posse.kotlin1.calendar.common.domain.repository.MessageHandler
import com.posse.kotlin1.calendar.feature_calendar.domain.model.DayData
import javax.inject.Inject

class SendMessage @Inject constructor(
    private val messageHandler: MessageHandler
) {
    suspend operator fun invoke(mail: String, day: DayData) {
        messageHandler.sendOrScheduleMessage(mail, day)
    }
}
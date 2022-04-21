package com.posse.kotlin1.calendar.common.domain.repository

import com.posse.kotlin1.calendar.common.domain.model.Message

interface Messenger {
    fun sendPush(message: Message)
}
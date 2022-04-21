package com.posse.kotlin1.calendar.common.domain.model

import com.posse.kotlin1.calendar.common.data.model.MessageType

data class Message(val email: String, val message: MessageType, val id: String, val drinkType: String? = null)

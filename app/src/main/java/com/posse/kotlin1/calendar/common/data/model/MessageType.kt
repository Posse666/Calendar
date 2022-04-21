package com.posse.kotlin1.calendar.common.data.model

sealed class MessageType(val value: Long) {
    object AddedYou : MessageType(-1)
    object RemovedYou : MessageType(-2)
    class Drunk(val date: Long) : MessageType(date)
}

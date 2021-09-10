package com.posse.kotlin1.calendar.firebaseMessagingService

data class Message(val to: String, val notification: Notification, val data: NotificationData, val direct_boot_ok: Boolean = true)
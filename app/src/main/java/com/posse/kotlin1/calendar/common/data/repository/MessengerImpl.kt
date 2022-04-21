package com.posse.kotlin1.calendar.common.data.repository

import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.RemoteMessage
import com.google.firebase.messaging.ktx.messaging
import com.google.firebase.messaging.ktx.remoteMessage
import com.posse.kotlin1.calendar.common.domain.model.Message
import com.posse.kotlin1.calendar.common.domain.repository.Messenger
import com.posse.kotlin1.calendar.firebaseMessagingService.MyFirebaseMessagingService
import java.util.*

class MessengerImpl : Messenger {
    override fun sendPush(message: Message) {
        val remoteMessage = getMessage(message)
        Firebase.messaging.send(remoteMessage)
    }

    private fun getMessage(message: Message): RemoteMessage {
        return remoteMessage("929390260810@fcm.googleapis.com") {
            messageId = getUniqueMessageId()
            addData(MyFirebaseMessagingService.PUSH_KEY_TITLE, message.email)
            addData(TO, message.id)
            addData(PAYLOAD_ATTRIBUTE_ACTION, BACKEND_ACTION_MESSAGE)
            addData(MyFirebaseMessagingService.PUSH_KEY_MESSAGE, message.message.value.toString())
            message.drinkType?.let {
                addData(MyFirebaseMessagingService.DRINK_TYPE, it)
            }
            ttl = 86400
        }
    }

    private fun getUniqueMessageId(): String = "m-" + UUID.randomUUID().toString()

    companion object {
        private const val TO = "recipient"
        private const val BACKEND_ACTION_MESSAGE = "MESSAGE"
        private const val PAYLOAD_ATTRIBUTE_ACTION = "action"
    }
}
package com.posse.kotlin1.calendar.firebaseMessagingService

import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging
import com.google.firebase.messaging.ktx.remoteMessage
import com.posse.kotlin1.calendar.firebaseMessagingService.MyFirebaseMessagingService.Companion.DRINK_TYPE
import com.posse.kotlin1.calendar.firebaseMessagingService.MyFirebaseMessagingService.Companion.PUSH_KEY_MESSAGE
import com.posse.kotlin1.calendar.firebaseMessagingService.MyFirebaseMessagingService.Companion.PUSH_KEY_TITLE
import java.util.*
import javax.inject.Inject

class Messenger @Inject constructor() {

    fun sendPush(name: String, message: String, id: String, drinkType: String? = null) {
        Firebase.messaging.send(remoteMessage("929390260810@fcm.googleapis.com") {
            setMessageId(getUniqueMessageId())
            addData(PUSH_KEY_TITLE, name)
            addData(TO, id)
            addData(PAYLOAD_ATTRIBUTE_ACTION, BACKEND_ACTION_MESSAGE)
            addData(PUSH_KEY_MESSAGE, message)
            drinkType?.let {
                addData(DRINK_TYPE, it)
            }
            setTtl(86400)
        })
    }

    private fun getUniqueMessageId(): String = "m-" + UUID.randomUUID().toString()

    companion object {
        private const val TO = "recipient"
        private const val BACKEND_ACTION_MESSAGE = "MESSAGE"
        private const val PAYLOAD_ATTRIBUTE_ACTION = "action"
    }
}
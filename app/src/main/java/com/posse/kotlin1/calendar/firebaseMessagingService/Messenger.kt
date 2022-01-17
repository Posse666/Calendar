package com.posse.kotlin1.calendar.firebaseMessagingService

import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import com.posse.kotlin1.calendar.R
import com.posse.kotlin1.calendar.firebaseMessagingService.MyFirebaseMessagingService.Companion.ADDED_YOU
import com.posse.kotlin1.calendar.firebaseMessagingService.MyFirebaseMessagingService.Companion.REMOVED_YOU
import com.posse.kotlin1.calendar.utils.convertLongToLocalDale
import java.time.LocalDate
import java.util.*
import javax.inject.Inject

class Messenger @Inject constructor(
    private val firebaseAPI: FirebaseAPI,
    private val context: Context
) {

    fun sendPush(title: String, message: String, id: String, locale: Locale) {
        val messageToSend = composeNotificationMessage(locale, message, title, id)
        firebaseAPI.sendMessage(messageToSend).execute()
    }

    private fun composeNotificationMessage(
        locale: Locale,
        message: String,
        title: String,
        id: String
    ): Message {
        val text: String = getNotificationText(locale, message)
        val notificationData = NotificationData(title, message)
        val notification = Notification("$title $text", "")
        return Message(id, notification, notificationData)
    }

    private fun getNotificationText(locale: Locale, message: String): String {
        val resources = getLocalizedResources(locale)
        return when (message) {
            ADDED_YOU.toString() -> resources.getString(R.string.shared_with_you)
            REMOVED_YOU.toString() -> resources.getString(R.string.removed_from_friends)
            else -> {
                val drunk = resources.getString(R.string.drunk)
                when (val date = convertLongToLocalDale(message.toLong())) {
                    LocalDate.now() -> "$drunk ${resources.getString(R.string.today)}!"
                    LocalDate.now()
                        .minusDays(1) -> "$drunk ${resources.getString(R.string.yesterday)}!"
                    else -> "$drunk $date!"
                }
            }
        }
    }

    private fun getLocalizedResources(desiredLocale: Locale?): Resources {
        var conf: Configuration = context.resources.configuration
        conf = Configuration(conf)
        conf.setLocale(desiredLocale)
        return context.createConfigurationContext(conf).resources
    }
}
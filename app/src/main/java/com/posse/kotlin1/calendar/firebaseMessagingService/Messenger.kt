package com.posse.kotlin1.calendar.firebaseMessagingService

import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import com.posse.kotlin1.calendar.R
import com.posse.kotlin1.calendar.app.App
import com.posse.kotlin1.calendar.utils.convertLongToLocalDale
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.time.LocalDate
import java.util.*


class Messenger {

    private val firebaseAPI = Retrofit.Builder()
        .baseUrl("https://fcm.googleapis.com")
        .client(OkHttpClient.Builder().build())
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(FirebaseAPI::class.java)

    fun sendPush(title: String, message: String, id: String, locale: Locale) {
        val resources = getLocalizedResources(locale)
        val text: String = when (message) {
            ADDED_YOU.toString() -> resources.getString(R.string.shared_with_you)
            REMOVED_YOU.toString() -> resources.getString(R.string.removed_from_friends)
            else -> {
                val drunk = resources.getString(R.string.drunk)
                when (val date = convertLongToLocalDale(message.toLong())) {
                    LocalDate.now() -> {
                        "$drunk ${resources.getString(R.string.today)}!"
                    }
                    LocalDate.now().minusDays(1) -> {
                        "$drunk ${resources.getString(R.string.yesterday)}!"
                    }
                    else -> {
                        "$drunk $date!"
                    }
                }
            }
        }
        val notificationData = NotificationData(title, message)
        val notification = Notification("$title $text", "")
        val messageToSend = Message(id, notification, notificationData)
        firebaseAPI.sendMessage(messageToSend).execute()
    }

    private fun getLocalizedResources(desiredLocale: Locale?): Resources {
        val context = App.appInstance
        var conf: Configuration = context.resources.configuration
        conf = Configuration(conf)
        conf.setLocale(desiredLocale)
        val localizedContext: Context = context.createConfigurationContext(conf)
        return localizedContext.resources
    }
}
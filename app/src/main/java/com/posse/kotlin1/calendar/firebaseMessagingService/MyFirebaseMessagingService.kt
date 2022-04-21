package com.posse.kotlin1.calendar.firebaseMessagingService

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.SharedPreferences
import android.content.res.Configuration
import android.content.res.Resources
import androidx.core.app.NotificationCompat
import androidx.core.content.getSystemService
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.posse.kotlin1.calendar.R
import com.posse.kotlin1.calendar.common.data.model.Documents
import com.posse.kotlin1.calendar.common.data.model.User
import com.posse.kotlin1.calendar.common.data.utils.convertLongToLocalDale
import com.posse.kotlin1.calendar.common.data.utils.toDataClass
import com.posse.kotlin1.calendar.model.repository.Repository
import com.posse.kotlin1.calendar.model.repository.RepositoryFirestoreImpl.Companion.COLLECTION_USERS
import com.posse.kotlin1.calendar.utils.*
import com.posse.kotlin1.calendar.view.calendar.DrinkType
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDate
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class MyFirebaseMessagingService : FirebaseMessagingService() {
    @Inject
    lateinit var repository: Repository

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    @Inject
    lateinit var localeUtils: LocaleUtils

    private lateinit var channelName: String
    private lateinit var channelDescriptionText: String
    private lateinit var channelID: String

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        val remoteMessageData = remoteMessage.data
        if (remoteMessageData.isNotEmpty()) {
            handleDataMessage(remoteMessageData.toMap())
        }
    }

    private fun handleDataMessage(data: Map<String, String>) {
        val name = data[PUSH_KEY_TITLE]  //  TODO get username from base by email
        val message = data[PUSH_KEY_MESSAGE]
        val drinkType = data[DRINK_TYPE]
        if (!name.isNullOrBlank() && !message.isNullOrBlank()) {
            showNotification(name, message, drinkType)
        }
    }

    private fun showNotification(name: String, message: String, drinkType: String?) {
        channelID = getString(R.string.drunk_channel)
        if (message == ADDED_YOU.toString() || message == REMOVED_YOU.toString()) channelID =
            getString(R.string.shared)
        val notificationBuilder = NotificationCompat.Builder(applicationContext, channelID)
            .apply {
                setSmallIcon(R.drawable.ic_splash_screen)
                setContentTitle(getText(name, message, drinkType))
                priority = NotificationCompat.PRIORITY_DEFAULT
            }

        getSystemService<NotificationManager>()?.let {
            createNotificationChannel(it)
            it.notify(NOTIFICATION_ID, notificationBuilder.build())
        }
    }

    private fun getText(name: String, message: String, drinkType: String?): String {
        val resources = getLocalizedResources(localeUtils.getLocale(sharedPreferences.locale))
        channelName = resources.getString(R.string.shared)
        channelDescriptionText = resources.getString(R.string.shared_notifications)
        val text: String = when (message) {
            ADDED_YOU.toString() -> resources.getString(R.string.shared_with_you)
            REMOVED_YOU.toString() -> resources.getString(R.string.removed_from_friends)
            else -> {
                channelName = resources.getString(R.string.drunk_channel)
                channelDescriptionText = resources.getString(R.string.drunk_notifications)
                val drunkResource = if (drinkType == DrinkType.Half.value) R.string.drunk_a_little
                else R.string.drunk
                val drunk = resources.getString(drunkResource)
                when (val date = convertLongToLocalDale(message.toLong())) {
                    LocalDate.now() -> "$drunk ${resources.getString(R.string.today)}!"
                    LocalDate.now()
                        .minusDays(1) -> "$drunk ${resources.getString(R.string.yesterday)}!"
                    else -> "$drunk $date!"
                }
            }
        }
        return "$name $text"
    }

    private fun createNotificationChannel(notificationManager: NotificationManager) {
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(channelID, channelName, importance).apply {
            description = channelDescriptionText
        }
        notificationManager.createNotificationChannel(channel)
    }

    private fun getLocalizedResources(desiredLocale: Locale?): Resources {
        var conf: Configuration = applicationContext.resources.configuration
        conf = Configuration(conf)
        conf.setLocale(desiredLocale)
        return applicationContext.createConfigurationContext(conf).resources
    }

    override fun onNewToken(token: String) {
        sharedPreferences.token = token
        sharedPreferences.nickName?.let {
            repository.getData(Documents.Users, COLLECTION_USERS) { users, _ ->
                users?.forEach { userMap ->
                    try {
                        @Suppress("UNCHECKED_CAST")
                        val user = (userMap.value as Map<String, Any>).toDataClass<User>()
                        if (user.nickname == it) repository.saveUser(
                            User(user.email, it, localeUtils.getStringLocale(), token)
                        )
                    } catch (e: Exception) {
                    }
                }
            }
        }
    }

    companion object {
        const val PUSH_KEY_TITLE = "title"
        const val PUSH_KEY_MESSAGE = "message"
        const val DRINK_TYPE = "drinkType"
        private const val NOTIFICATION_ID = 68
    }
}
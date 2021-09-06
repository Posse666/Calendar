package com.posse.kotlin1.calendar.firebaseMessagingService

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.posse.kotlin1.calendar.app.App
import com.posse.kotlin1.calendar.model.User
import com.posse.kotlin1.calendar.model.repository.COLLECTION_USERS
import com.posse.kotlin1.calendar.model.repository.DOCUMENTS
import com.posse.kotlin1.calendar.model.repository.Repository
import com.posse.kotlin1.calendar.model.repository.RepositoryFirestoreImpl
import com.posse.kotlin1.calendar.utils.nickName
import com.posse.kotlin1.calendar.utils.toDataClass
import com.posse.kotlin1.calendar.utils.token

class MyFirebaseMessagingService : FirebaseMessagingService() {
    private val repository: Repository = RepositoryFirestoreImpl.newInstance()

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        val remoteMessageData = remoteMessage.data
        if (remoteMessageData.isNotEmpty()) {
            handleDataMessage(remoteMessageData.toMap())
        }
    }

    private fun handleDataMessage(data: Map<String, String>) {
        val title = data[PUSH_KEY_TITLE]
        val message = data[PUSH_KEY_MESSAGE]
        if (!title.isNullOrBlank() && !message.isNullOrBlank()) {
            showNotification(title, message)
        }
    }

    private fun showNotification(title: String, message: String) {
        val notificationBuilder =
            NotificationCompat.Builder(applicationContext, CHANNEL_ID).apply {
                setSmallIcon(android.R.drawable.checkbox_on_background)
                setContentTitle(title)
//                setContentText(message)
                priority = NotificationCompat.PRIORITY_DEFAULT
            }

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        createNotificationChannel(notificationManager)
        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build())
    }

    private fun createNotificationChannel(notificationManager: NotificationManager) {
        val name = "Channel name"
        val descriptionText = "Channel description"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
            description = descriptionText
        }
        notificationManager.createNotificationChannel(channel)
    }

    override fun onNewToken(token: String) {
        App.sharedPreferences?.token = token
        App.sharedPreferences?.nickName?.let {
            repository.getData(DOCUMENTS.USERS, COLLECTION_USERS) { users, _ ->
                users?.forEach { userMap ->
                    try {
                        val user = (userMap.value as Map<String, Any>).toDataClass<User>()
                        if (user.nickname == it) repository.saveUser(User(user.email, it, token))
                    } catch (e: Exception) {
                    }
                }
            }
        }
    }

    companion object {
        private const val PUSH_KEY_TITLE = "title"
        private const val PUSH_KEY_MESSAGE = "message"
        private const val CHANNEL_ID = "channel_id"
        private const val NOTIFICATION_ID = 68
    }
}
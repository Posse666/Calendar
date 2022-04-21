package com.posse.kotlin1.calendar.common.data.repository

import com.posse.kotlin1.calendar.common.data.model.MessageType
import com.posse.kotlin1.calendar.common.domain.model.Message
import com.posse.kotlin1.calendar.common.domain.repository.FriendsRepository
import com.posse.kotlin1.calendar.common.domain.repository.MessageHandler
import com.posse.kotlin1.calendar.common.domain.repository.Messenger
import com.posse.kotlin1.calendar.common.domain.repository.UsersRepository
import com.posse.kotlin1.calendar.common.domain.utils.DispatcherProvider
import com.posse.kotlin1.calendar.common.domain.utils.NetworkStatus
import com.posse.kotlin1.calendar.feature_calendar.domain.model.DayData
import kotlinx.coroutines.withContext
import java.io.IOException

class MessageHandlerImpl(
    private val messenger: Messenger,
    private val networkStatus: NetworkStatus,
    private val usersRepository: UsersRepository,
    private val friendsRepository: FriendsRepository,
    private val dispatcherProvider: DispatcherProvider,
//    private val workManager: WorkManager
) : MessageHandler {

    override suspend fun sendOrScheduleMessage(userMail: String, day: DayData) {
        if (day.drinkType == null) return
        withContext(dispatcherProvider.io) {
            val messages = composeMessages(userMail, day)
            if (networkStatus.isNetworkOnline())
                try {
                    messages.forEach { message ->
                        messenger.sendPush(message)
                    }
                } catch (e: IOException) {
                    startWorkManager()
                }
            else {
                startWorkManager()
            }
        }
    }

    private fun startWorkManager() {
        //TODO
    }

    private suspend fun composeMessages(userMail: String, day: DayData): List<Message> {
        val messages = mutableListOf<Message>()

        val users = usersRepository.getAllUsers()
        val friends = friendsRepository.getFriends(userMail)

        friends.forEach { friend ->
            val user = users.find { it.email == friend.email }
            user?.token?.let { token ->

                val message = Message(
                    email = userMail,
                    message = MessageType.Drunk(day.date),
                    id = token,
                    drinkType = day.drinkType
                )

                messages.add(message)
            }
        }

        return messages
    }
}
package com.posse.kotlin1.calendar.common.data.repository

import com.posse.kotlin1.calendar.common.data.model.MessageType
import com.posse.kotlin1.calendar.common.data.utils.convertLocalDateToLong
import com.posse.kotlin1.calendar.common.domain.model.Message
import com.posse.kotlin1.calendar.common.domain.repository.FriendsRepository
import com.posse.kotlin1.calendar.common.domain.repository.MessageHandler
import com.posse.kotlin1.calendar.common.domain.repository.Messenger
import com.posse.kotlin1.calendar.common.domain.repository.UsersRepository
import com.posse.kotlin1.calendar.common.domain.utils.DispatcherProvider
import com.posse.kotlin1.calendar.common.domain.utils.NetworkStatus
import com.posse.kotlin1.calendar.feature_calendar.domain.model.DayData
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.io.IOException
import javax.inject.Inject

class MessageHandlerImpl @Inject constructor(
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
            val messages = composeMessages(userMail, day).filterNotNull()
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

    private suspend fun composeMessages(userMail: String, day: DayData): List<Message?> {
        val users = usersRepository.getAllUsers()
        return friendsRepository
            .getFriends(userMail)
            .first()
            .map { friend ->
                users
                    .find { it.email == friend.email }
                    ?.token
                    ?.let { token ->
                        Message(
                            email = userMail,
                            message = MessageType.Drunk(convertLocalDateToLong(day.date)),
                            id = token,
                            drinkType = day.drinkType?.value
                        )
                    }
            }
    }
}
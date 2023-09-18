package com.posse.kotlin1.calendar.common.data.repository

import com.posse.kotlin1.calendar.common.domain.repository.MessageHandler
import com.posse.kotlin1.calendar.common.utils.CoroutineDispatchers
import com.posse.kotlin1.calendar.feature_calendar.domain.model.DayData
import kotlinx.coroutines.withContext

class MessageHandlerImpl(
//    private val messenger: Messenger,
//    private val usersRepository: UsersRepository,
//    private val friendsRepository: FriendsRepository,
    private val coroutineDispatchers: CoroutineDispatchers
) : MessageHandler {

    override suspend fun sendMessage(userMail: String, day: DayData) {
        if (day.drinkType == null) return
        withContext(coroutineDispatchers.io) {
//            val messages = composeMessages(userMail, day).filterNotNull()
//            messages.forEach { message ->
//                messenger.sendPush(message)
//            }
        }
    }

//    private suspend fun composeMessages(userMail: String, day: DayData): List<Message?> {
//        val users = usersRepository.getAllUsers()
//        return friendsRepository
//            .getFriends(userMail)
//            .first()
//            .map { friend ->
//                users
//                    .find { it.email == friend.email }
//                    ?.token
//                    ?.let { token ->
//                        Message(
//                            email = userMail,
//                            message = MessageType.Drunk(convertLocalDateToLong(day.date)),
//                            id = token,
//                            drinkType = day.drinkType?.value
//                        )
//                    }
//            }
//    }
}
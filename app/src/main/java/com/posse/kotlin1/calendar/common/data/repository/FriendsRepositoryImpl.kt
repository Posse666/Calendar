package com.posse.kotlin1.calendar.common.data.repository

import com.posse.kotlin1.calendar.common.data.model.Documents
import com.posse.kotlin1.calendar.common.data.model.FriendEntity
import com.posse.kotlin1.calendar.common.data.utils.toFriend
import com.posse.kotlin1.calendar.common.data.utils.toFriendEntity
import com.posse.kotlin1.calendar.common.domain.model.Friend
import com.posse.kotlin1.calendar.common.domain.repository.FriendsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class FriendsRepositoryImpl @Inject constructor(
    private val peopleRepository: PeopleRepository,
    private val firestoreRepository: FirestoreRepository,
) : FriendsRepository {

    override fun getFriends(email: String): Flow<List<Friend>> {
        return peopleRepository
            .getPeople<FriendEntity>(email, Documents.Friends)
            .map { it.map { friendEntity -> friendEntity.toFriend() } }
    }

    override suspend fun saveFriend(userMail: String, friend: Friend): Boolean {
        return changeFriend(
            userMail = userMail,
            friend = friend,
            delete = false
        )
    }

    override suspend fun deleteFriend(userMail: String, friend: Friend): Boolean {
        return changeFriend(
            userMail = userMail,
            friend = friend,
            delete = !friend.isBlocked
        )
    }

    private suspend fun changeFriend(userMail: String, friend: Friend, delete: Boolean): Boolean {
        return firestoreRepository
            .changeItem(
                collection = userMail,
                document = Documents.Friends,
                data = friend.toFriendEntity(),
                delete = delete
            )
    }
}
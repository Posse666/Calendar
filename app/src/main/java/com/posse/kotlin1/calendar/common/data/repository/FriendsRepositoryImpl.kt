package com.posse.kotlin1.calendar.common.data.repository

import com.posse.kotlin1.calendar.common.data.model.Documents
import com.posse.kotlin1.calendar.common.data.model.Friend
import com.posse.kotlin1.calendar.common.domain.repository.FriendsRepository
import javax.inject.Inject

class FriendsRepositoryImpl @Inject constructor(
    private val peopleRepository: PeopleRepository,
    private val firestoreRepository: FirestoreRepository,
) : FriendsRepository {

    override suspend fun getFriends(email: String): List<Friend> =
        peopleRepository.getPeople(email, Documents.Friends)

    override suspend fun saveFriend(userMail: String, friend: Friend): Boolean {
        return firestoreRepository
            .changeItem(
                collection = userMail,
                document = Documents.Friends,
                data = friend,
                delete = false
            )
    }
}
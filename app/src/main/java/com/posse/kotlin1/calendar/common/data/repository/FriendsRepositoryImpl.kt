package com.posse.kotlin1.calendar.common.data.repository

import com.posse.kotlin1.calendar.common.data.model.Documents
import com.posse.kotlin1.calendar.common.domain.repository.FriendsRepository
import com.posse.kotlin1.calendar.common.data.model.Friend

class FriendsRepositoryImpl(
    private val peopleRepository: PeopleRepository
) : FriendsRepository {

    override suspend fun getFriends(email: String): List<Friend> =
        peopleRepository.getPeople(email, Documents.Friends)
}
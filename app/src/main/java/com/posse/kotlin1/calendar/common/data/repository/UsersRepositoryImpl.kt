package com.posse.kotlin1.calendar.common.data.repository

import com.posse.kotlin1.calendar.common.data.model.Documents
import com.posse.kotlin1.calendar.common.domain.repository.UsersRepository
import com.posse.kotlin1.calendar.common.data.model.User

class UsersRepositoryImpl(
    private val peopleRepository: PeopleRepository
) : UsersRepository {

    override suspend fun getAllUsers(): List<User> =
        peopleRepository.getPeople(ALL_USERS, Documents.Users)

    companion object {
        private const val ALL_USERS = "Collection_of_all_users"
    }
}
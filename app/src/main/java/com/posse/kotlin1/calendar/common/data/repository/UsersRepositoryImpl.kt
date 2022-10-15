package com.posse.kotlin1.calendar.common.data.repository

import com.posse.kotlin1.calendar.common.data.model.Documents
import com.posse.kotlin1.calendar.common.domain.repository.UsersRepository
import com.posse.kotlin1.calendar.common.data.model.User
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class UsersRepositoryImpl @Inject constructor(
    private val peopleRepository: PeopleRepository
) : UsersRepository {

    override suspend fun getAllUsers(): List<User> =
        peopleRepository.getPeople<User>(ALL_USERS, Documents.Users).first()

    companion object {
        private const val ALL_USERS = "Collection_of_all_users"
    }
}
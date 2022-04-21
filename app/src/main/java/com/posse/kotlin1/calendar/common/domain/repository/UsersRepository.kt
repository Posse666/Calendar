package com.posse.kotlin1.calendar.common.domain.repository

import com.posse.kotlin1.calendar.common.data.model.User

interface UsersRepository {
    suspend fun getAllUsers(): List<User>
}
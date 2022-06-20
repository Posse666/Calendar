package com.posse.kotlin1.calendar.common.domain.repository

import com.posse.kotlin1.calendar.common.domain.model.User

interface AccountRepository {
    suspend fun getMyMail(): String?
    fun getCurrentUser(): User?
}
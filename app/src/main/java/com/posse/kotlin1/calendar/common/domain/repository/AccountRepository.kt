package com.posse.kotlin1.calendar.common.domain.repository

import com.posse.kotlin1.calendar.common.domain.model.User

interface AccountRepository {
    fun getMyEmailOrId(): String
    fun getCurrentUser(): User?
}
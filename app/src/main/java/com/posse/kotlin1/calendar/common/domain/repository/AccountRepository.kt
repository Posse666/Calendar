package com.posse.kotlin1.calendar.common.domain.repository

interface AccountRepository {
    suspend fun getMyMail(): String?
}
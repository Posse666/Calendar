package com.posse.kotlin1.calendar.common.domain.repository

import com.posse.kotlin1.calendar.common.domain.model.Friend
import kotlinx.coroutines.flow.Flow

interface FriendsRepository {
    fun getFriends(email: String): Flow<List<Friend>>
    suspend fun saveFriend(userMail: String, friend: Friend): Boolean
    suspend fun deleteFriend(userMail: String, friend: Friend): Boolean
}
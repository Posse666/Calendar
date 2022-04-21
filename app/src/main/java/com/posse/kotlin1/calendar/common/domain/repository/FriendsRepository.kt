package com.posse.kotlin1.calendar.common.domain.repository

import com.posse.kotlin1.calendar.common.data.model.Friend

interface FriendsRepository {
    suspend fun getFriends(email: String): List<Friend>
    suspend fun saveFriend(userMail: String, friend: Friend): Boolean
}
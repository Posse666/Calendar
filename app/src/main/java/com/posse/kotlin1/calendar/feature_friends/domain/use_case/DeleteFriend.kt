package com.posse.kotlin1.calendar.feature_friends.domain.use_case

import com.posse.kotlin1.calendar.common.domain.model.Friend
import com.posse.kotlin1.calendar.common.domain.repository.FriendsRepository
import javax.inject.Inject

class DeleteFriend @Inject constructor(
    private val repository: FriendsRepository
) {
    suspend operator fun invoke(userEmail: String, friend: Friend): Boolean {
        return repository.deleteFriend(userEmail, friend)
    }
}
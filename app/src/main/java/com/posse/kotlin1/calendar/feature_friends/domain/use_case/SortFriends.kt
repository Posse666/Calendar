package com.posse.kotlin1.calendar.feature_friends.domain.use_case

import com.posse.kotlin1.calendar.common.data.model.Friend
import com.posse.kotlin1.calendar.common.domain.repository.FriendsRepository
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

class SortFriends @Inject constructor(
    private val repository: FriendsRepository
) {
    suspend operator fun invoke(mail: String, friends: List<Friend>): List<Friend> {
        return friends.mapIndexed { index, friend ->
            if (friend.position != index) {
                friend.copy(position = index).also {
                    coroutineScope {
                        launch {
                            repository.saveFriend(mail, it)
                        }
                    }
                }
            } else friend
        }
    }
}
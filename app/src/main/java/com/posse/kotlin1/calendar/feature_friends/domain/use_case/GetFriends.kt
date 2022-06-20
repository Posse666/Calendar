package com.posse.kotlin1.calendar.feature_friends.domain.use_case

import com.posse.kotlin1.calendar.common.domain.model.Friend
import com.posse.kotlin1.calendar.common.domain.repository.FriendsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetFriends @Inject constructor(
    private val friendsRepository: FriendsRepository
) {
    operator fun invoke(email: String): Flow<List<Friend>> {
        return friendsRepository
            .getFriends(email)
            .map { friendsList ->
                friendsList
                    .filter { !it.isBlocked }
                    .sortedWith(
                        compareBy<Friend> { it.isSelected }.thenBy { it.name }
                    )
            }
    }
}
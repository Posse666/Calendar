package com.posse.kotlin1.calendar.feature_friends.presentation.model

import com.posse.kotlin1.calendar.common.domain.model.Friend

sealed class FriendsEvent {
    data class FriendSelected(val friend: Friend): FriendsEvent()
    data class FriendNameChanged(val name: String, val friend: Friend): FriendsEvent()
    data class EditPressed(val friend: Friend): FriendsEvent()
    data class DeleteFriend(val isBlocked: Boolean, val friend: Friend): FriendsEvent()
}

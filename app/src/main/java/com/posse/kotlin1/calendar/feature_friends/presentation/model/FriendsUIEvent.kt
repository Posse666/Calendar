package com.posse.kotlin1.calendar.feature_friends.presentation.model

import com.posse.kotlin1.calendar.common.domain.model.Friend

sealed class FriendsUIEvent {
    object ErrorLoading : FriendsUIEvent()
    data class FriendSelected(val friend: Friend) : FriendsUIEvent()
}
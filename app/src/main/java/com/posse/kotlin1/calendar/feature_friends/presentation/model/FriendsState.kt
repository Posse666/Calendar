package com.posse.kotlin1.calendar.feature_friends.presentation.model

import com.posse.kotlin1.calendar.common.data.model.Friend

data class FriendsState(
    val isLoading: Boolean = false,
    val friends: List<Friend> = emptyList()
)
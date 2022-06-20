package com.posse.kotlin1.calendar.feature_friends.presentation.model

import com.posse.kotlin1.calendar.common.domain.model.Friend

data class FriendsState(
    val isLoading: Boolean = false,
    val friends: List<Friend> = emptyList()
)
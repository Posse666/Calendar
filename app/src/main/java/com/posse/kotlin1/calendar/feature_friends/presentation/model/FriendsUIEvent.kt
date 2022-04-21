package com.posse.kotlin1.calendar.feature_friends.presentation.model

sealed class FriendsUIEvent {
    object ErrorLoading: FriendsUIEvent()
}
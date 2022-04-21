package com.posse.kotlin1.calendar.feature_friends.domain.use_case

import javax.inject.Inject

data class FriendsUseCases @Inject constructor(
    val getFriends: GetFriends,
    val sortFriends: SortFriends
)
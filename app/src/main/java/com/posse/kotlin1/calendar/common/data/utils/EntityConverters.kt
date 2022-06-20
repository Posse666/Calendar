package com.posse.kotlin1.calendar.common.data.utils

import com.posse.kotlin1.calendar.common.data.model.FriendEntity
import com.posse.kotlin1.calendar.common.domain.model.Friend

fun Friend.toFriendEntity(): FriendEntity {
    return FriendEntity(
        name = name,
        email = email,
        selected = isSelected,
        blocked = isBlocked
    )
}

fun FriendEntity.toFriend(): Friend {
    return Friend(
        name = name,
        email = email,
        isBlocked = blocked,
        isSelected = selected
    )
}
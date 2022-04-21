package com.posse.kotlin1.calendar.feature_friends.domain.use_case

import com.posse.kotlin1.calendar.common.data.model.Documents
import com.posse.kotlin1.calendar.common.data.model.Friend

class SortFriends {
    operator fun invoke(friends: Friend): List<Friend> {
        for (i in list.indices) {
            if (list[i].position != i) {
                list[i].position = i
                repository.saveItem(Documents.Friends, email, list[i])
            }
        }
    }
}
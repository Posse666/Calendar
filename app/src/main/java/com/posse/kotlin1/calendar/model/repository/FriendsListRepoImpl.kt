package com.posse.kotlin1.calendar.model.repository

import com.posse.kotlin1.calendar.model.Friend

class FriendsListRepoImpl : FriendsListRepo {
    override fun getListOfFriends(): Set<Friend> {
        return setOf(
            Friend(0, "Andrey", "andreyMail@mail.ru", false),
            Friend(1, "Sergey", "sergeyMail@mail.ru", false),
            Friend(2, "Viktor", "ViktorMail@mail.ru", false),
            Friend(3, "Leonid", "LeonidMail@mail.ru", false)
        )
    }
}
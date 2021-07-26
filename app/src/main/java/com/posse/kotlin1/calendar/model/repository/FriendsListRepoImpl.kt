package com.posse.kotlin1.calendar.model.repository

import com.posse.kotlin1.calendar.model.Friend

class FriendsListRepoImpl : FriendsListRepo {
    override fun getListOfFriends(): Set<Friend> {
        return setOf(
            Friend("Andrey","andreyMail@mail.ru", false),
            Friend("Sergey","sergeyMail@mail.ru", false),
            Friend("Viktor","ViktorMail@mail.ru", false),
            Friend("Leonid","LeonidMail@mail.ru", false))
    }
}
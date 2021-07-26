package com.posse.kotlin1.calendar.model.repository

import com.posse.kotlin1.calendar.model.Friend

interface FriendsListRepo{

    fun getListOfFriends(): Set<Friend>
}
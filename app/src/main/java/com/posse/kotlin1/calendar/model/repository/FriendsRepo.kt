package com.posse.kotlin1.calendar.model.repository

import androidx.lifecycle.LiveData
import com.posse.kotlin1.calendar.model.Friend
import java.util.*

interface FriendsRepo : BaseRepo {

    fun getLiveData(): LiveData<HashSet<Friend>>

    fun refreshData()

    fun saveFriend(friend: Friend)

    fun changePosition(fromPosition: Int, toPosition: Int)

    fun deleteItem(friend: Friend)
}
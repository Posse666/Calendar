package com.posse.kotlin1.calendar.model.repository

import androidx.lifecycle.LiveData
import com.posse.kotlin1.calendar.model.Friend
import com.posse.kotlin1.calendar.model.Contact
import java.time.LocalDate

interface Repository {

    fun switchCollection(email: String)

    fun mergeDates(newMail: String, nickName: String)

    fun getDatesLiveData(): LiveData<Pair<Boolean, Set<LocalDate>>>

    fun changeState(date: LocalDate)

    fun getFriendsLiveData(): LiveData<Pair<Boolean, Set<Friend>>>

    fun updateFriendsData()

    fun saveFriend(friend: Friend)

    fun changeFriendPosition(fromPosition: Int, toPosition: Int)

    fun deleteFriend(friend: Friend)

    fun getSharedLiveData(): LiveData<Pair<Boolean, Set<Contact>>>

    fun setContacts(contacts: List<Contact>)

    fun contactClicked(contact: Contact)
}
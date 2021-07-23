package com.posse.kotlin1.calendar.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.posse.kotlin1.calendar.model.Friend
import com.posse.kotlin1.calendar.model.repository.FriendsListRepo
import com.posse.kotlin1.calendar.model.repository.FriendsListRepoImpl

class FriendsListViewModel : ViewModel() {
    private val repository: FriendsListRepo = FriendsListRepoImpl()
    private val liveDataToObserve: MutableLiveData<Set<Friend>> = MutableLiveData()

    fun getLiveData() = liveDataToObserve
}
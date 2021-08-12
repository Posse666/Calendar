package com.posse.kotlin1.calendar.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.posse.kotlin1.calendar.model.Friend
import com.posse.kotlin1.calendar.model.repository.FriendsRepo
import com.posse.kotlin1.calendar.model.repository.FriendsRepoImpl

class FriendsViewModel : BaseViewModel() {
    override val repository: FriendsRepo = FriendsRepoImpl()
    private val liveDataToObserve: LiveData<Set<Friend>> = Transformations.map(repository.getLiveData()) { it }

    fun getLiveData() = liveDataToObserve

    fun refreshLivedata() = repository.refreshData()

    fun friendSelected(friend: Friend) = repository.saveFriend(friend)

    fun itemMoved(fromPosition: Int, toPosition: Int) = repository.changePosition(fromPosition, toPosition)

    fun deleteFriend(friend: Friend) = repository.deleteItem(friend)
}
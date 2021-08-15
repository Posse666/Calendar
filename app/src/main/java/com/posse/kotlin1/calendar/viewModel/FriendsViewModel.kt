package com.posse.kotlin1.calendar.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.posse.kotlin1.calendar.model.Friend
import com.posse.kotlin1.calendar.model.repository.Repository
import com.posse.kotlin1.calendar.model.repository.RepositoryFirestoreImpl

class FriendsViewModel : ViewModel() {
    private val repository: Repository = RepositoryFirestoreImpl
    private val liveDataToObserve: LiveData<Pair<Boolean, Set<Friend>>> =
        Transformations.map(repository.getFriendsLiveData()) { it }

    fun getLiveData() = liveDataToObserve

    fun refreshLiveData() = repository.updateFriendsData()

    fun friendSelected(friend: Friend) = repository.saveFriend(friend)

    fun itemMoved(fromPosition: Int, toPosition: Int) =
        repository.changeFriendPosition(fromPosition, toPosition)

    fun deleteFriend(friend: Friend) = repository.deleteFriend(friend)
}
package com.posse.kotlin1.calendar.feature_friends.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.posse.kotlin1.calendar.common.data.model.Contact
import com.posse.kotlin1.calendar.common.data.model.Documents
import com.posse.kotlin1.calendar.common.data.model.Friend
import com.posse.kotlin1.calendar.common.data.utils.toDataClass
import com.posse.kotlin1.calendar.common.domain.use_case.AccountUseCases
import com.posse.kotlin1.calendar.feature_friends.domain.use_case.FriendsUseCases
import com.posse.kotlin1.calendar.feature_friends.presentation.model.FriendsState
import com.posse.kotlin1.calendar.feature_friends.presentation.model.FriendsUIEvent
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class FriendsViewModel @Inject constructor(
    private val accountUseCases: AccountUseCases,
    private val friendsUseCases: FriendsUseCases
) : ViewModel() {

    private var mail: String? = null

    private val _state = MutableStateFlow(FriendsState())
    val state get() = _state.asStateFlow()

    private val _event = MutableSharedFlow<FriendsUIEvent>()
    val event get() = _event.asSharedFlow()

    init {
        try {
            viewModelScope.launch {
                mail = accountUseCases.getMyMail()

                mail?.let { mail ->
                    val friends = friendsUseCases.getFriends(mail)
                    setFriends(friends)
                } ?: handleError()
            }
        } catch (e: Exception) {
            handleError()
        }
    }


    private val friendsData: MutableSet<Friend> = mutableSetOf()
    private lateinit var email: String
    private val liveDataToObserve: MutableLiveData<Pair<Boolean, Set<Friend>>> =
        MutableLiveData(Pair(false, hashSetOf()))

    fun getLiveData(): LiveData<Pair<Boolean, Set<Friend>>> = liveDataToObserve

    fun refreshLiveData(email: String, callback: ((Boolean?) -> Unit)) {
        this.email = email
        liveDataToObserve.value = Pair(false, emptySet())
        repository.getData(Documents.Friends, email) { friends, isOffline ->
            friendsData.clear()
            try {
                friends?.values?.forEach { friendMap ->
                    @Suppress("UNCHECKED_CAST")
                    val friend = (friendMap as Map<String, Any>).toDataClass<Friend>()
                    if (!friend.blocked) friendsData.add(friend)
                }
            } catch (e: Exception) {
                callback(null)
            }
            sortPositions(friendsData.toList().sortedBy { it.position })
            liveDataToObserve.value = Pair(true, friendsData)
            if (isOffline) callback(isOffline)
        }
    }

    private fun sortPositions(list: List<Friend>) {
        for (i in list.indices) {
            if (list[i].position != i) {
                list[i].position = i
                repository.saveItem(Documents.Friends, email, list[i])
            }
        }
        friendsData.clear()
        friendsData.addAll(list)
    }

    fun friendSelected(friend: Friend) {
        var update = false
        var noOneIsSelected = true
        friendsData.forEach {
            if (it.selected) noOneIsSelected = false
            if (it.email == friend.email) {
                if (it.selected == friend.selected) update = true
                it.name = friend.name
                it.selected = friend.selected
                repository.saveItem(Documents.Friends, email, it)
            } else if (friend.selected && it.selected) {
                it.selected = false
                repository.saveItem(Documents.Friends, email, it)
                update = true
            }
        }
        if (update || noOneIsSelected) liveDataToObserve.value = Pair(true, friendsData)
    }

    fun itemMoved(fromPosition: Int, toPosition: Int) {
        friendsData.forEach {
            if (it.position == fromPosition) {
                it.position = toPosition
                repository.saveItem(Documents.Friends, email, it)
            } else if (it.position == toPosition) {
                it.position = fromPosition
                repository.saveItem(Documents.Friends, email, it)
            }
        }
    }

    fun changeName(friend: Friend) = repository.saveItem(Documents.Friends, email, friend)

    fun deleteFriend(friend: Friend, callback: ((Boolean?) -> Unit)) {
        if (friend.blocked) repository.saveItem(Documents.Friends, email, friend)
        else repository.removeItem(Documents.Friends, email, friend)
        repository.removeItem(Documents.Share, friend.email, Contact(mutableListOf(), email))
        refreshLiveData(email, callback)
    }

    private fun handleError() {
        viewModelScope.launch {
            _event.emit(FriendsUIEvent.ErrorLoading)
        }
        setLoadingState(isLoading = false)
    }

    private fun setFriends(friends: List<Friend>) {
        _state.value = state.value.copy(friends = friends)
        setLoadingState(isLoading = false)
    }

    private fun setLoadingState(isLoading: Boolean) {
        _state.value = state.value.copy(isLoading = isLoading)
    }
}
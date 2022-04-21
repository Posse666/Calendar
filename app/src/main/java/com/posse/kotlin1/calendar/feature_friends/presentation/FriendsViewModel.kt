package com.posse.kotlin1.calendar.feature_friends.presentation

import androidx.lifecycle.*
import com.posse.kotlin1.calendar.common.data.model.Contact
import com.posse.kotlin1.calendar.common.data.model.Documents
import com.posse.kotlin1.calendar.common.data.model.Friend
import com.posse.kotlin1.calendar.common.data.utils.toDataClass
import com.posse.kotlin1.calendar.common.domain.use_case.AccountUseCases
import com.posse.kotlin1.calendar.feature_friends.domain.use_case.FriendsUseCases
import com.posse.kotlin1.calendar.feature_friends.presentation.model.FriendsState
import com.posse.kotlin1.calendar.feature_friends.presentation.model.FriendsUIEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FriendsViewModel @Inject constructor(
    private val accountUseCases: AccountUseCases,
    private val friendsUseCases: FriendsUseCases,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private var mail: String? = savedStateHandle["email"]

    private val _state = MutableStateFlow(FriendsState())
    val state get() = _state.asStateFlow()

    private val _event = MutableSharedFlow<FriendsUIEvent>()
    val event get() = _event.asSharedFlow()

    init {
        setLoadingState(isLoading = true)
        if (mail == null) {
            try {
                viewModelScope.launch {
                    mail = accountUseCases.getMyMail()

                    mail?.let { mail ->
                        val friends = friendsUseCases.getFriends(mail)
                        val sortedFriends = friendsUseCases.sortFriends(mail, friends)
                        setFriends(sortedFriends)
                    } ?: handleError()
                }
            } catch (e: Exception) {
                handleError()
            }
        }
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
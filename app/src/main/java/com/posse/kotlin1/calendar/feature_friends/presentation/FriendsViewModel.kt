package com.posse.kotlin1.calendar.feature_friends.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.posse.kotlin1.calendar.common.domain.model.Friend
import com.posse.kotlin1.calendar.common.domain.use_case.AccountUseCases
import com.posse.kotlin1.calendar.feature_friends.domain.use_case.FriendsUseCases
import com.posse.kotlin1.calendar.feature_friends.presentation.model.FriendsEvent
import com.posse.kotlin1.calendar.feature_friends.presentation.model.FriendsState
import com.posse.kotlin1.calendar.feature_friends.presentation.model.FriendsUIEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
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

    private val _event = Channel<FriendsUIEvent>()
    val event get() = _event.receiveAsFlow()

    init {
        setLoadingState(isLoading = true)
        try {
            viewModelScope.launch {
                if (mail == null) mail = accountUseCases.getMyMail()

                mail?.let { mail ->
                    friendsUseCases.getFriends(mail).collect {
                        setFriends(it)
                    }
                } ?: handleError()
            }
        } catch (e: Exception) {
            handleError()
        }
        setLoadingState(isLoading = false)
    }

    fun onEvent(event: FriendsEvent) {
        when (event) {
            is FriendsEvent.FriendSelected -> friendSelected(event.friend)
            is FriendsEvent.FriendNameChanged -> changeName(event.name, event.friend)
            is FriendsEvent.EditPressed -> changeEditState(event.friend)
            is FriendsEvent.DeleteFriend -> deleteFriend(event.isBlocked, event.friend)
        }
    }

    private fun changeEditState(friend: Friend) {
        val friends = state.value.friends.map {
            if (it.email == friend.email) friend.copy(isEditable = true)
            else it
        }
        setFriends(friends)
    }

    private fun friendSelected(friend: Friend) {
        mail?.let { mail ->
            viewModelScope.launch {
                val newFriend = friend.copy(
                    isSelected = !friend.isSelected
                )
                friendsUseCases.saveFriend(mail, newFriend)
                _event.send(FriendsUIEvent.FriendSelected(friend = friend))
            }
        } ?: handleError()
    }

    private fun changeName(name: String, friend: Friend) {
        mail?.let { mail ->
            viewModelScope.launch {
                val newFriend = friend.copy(
                    name = name,
                    isEditable = false
                )
                friendsUseCases.saveFriend(mail, newFriend)
            }
        } ?: handleError()
    }

    private fun deleteFriend(isBlocked: Boolean, friend: Friend) {
        mail?.let { mail ->
            viewModelScope.launch {
                val deletedFriend = friend.copy(
                    isBlocked = isBlocked
                )
                friendsUseCases.deleteFriend(mail, deletedFriend)
            }
        } ?: handleError()
    }

    private fun handleError() {
        viewModelScope.launch {
            _event.send(FriendsUIEvent.ErrorLoading)
        }
        setLoadingState(isLoading = false)
    }

    private fun setFriends(friends: List<Friend>) {
        _state.value = state.value.copy(friends = friends)
    }

    private fun setLoadingState(isLoading: Boolean) {
        _state.value = state.value.copy(isLoading = isLoading)
    }
}
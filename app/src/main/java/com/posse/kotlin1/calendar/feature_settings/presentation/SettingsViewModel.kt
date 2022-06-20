package com.posse.kotlin1.calendar.feature_settings.presentation

import android.content.SharedPreferences
import androidx.annotation.StyleRes
import androidx.lifecycle.ViewModel
import com.posse.kotlin1.calendar.common.data.model.Documents
import com.posse.kotlin1.calendar.common.data.model.User
import com.posse.kotlin1.calendar.common.data.utils.toDataClass
import com.posse.kotlin1.calendar.feature_settings.domain.use_cases.SettingsUseCases
import com.posse.kotlin1.calendar.feature_settings.presentation.model.SettingsEvent
import com.posse.kotlin1.calendar.feature_settings.presentation.model.SettingsState
import com.posse.kotlin1.calendar.feature_settings.presentation.model.SettingsUIEvent
import com.posse.kotlin1.calendar.model.repository.Repository
import com.posse.kotlin1.calendar.model.repository.RepositoryFirestoreImpl
import com.posse.kotlin1.calendar.utils.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val sharedPreferences: SharedPreferences,
    private val settingsUseCases: SettingsUseCases
) : ViewModel() {

    private val _state = MutableStateFlow(SettingsState(
        user = settingsUseCases.getUser()
    ))
    val state get() = _state.asStateFlow()

    private val _event = Channel<SettingsUIEvent>()
    val event get() = _event.receiveAsFlow()

    fun onEvent(event: SettingsEvent){
        when (event){
            is SettingsEvent.BlackListPressed -> openBlackList()
            is SettingsEvent.EditNicknamePressed -> editNickname()
            is SettingsEvent.LoginPressed -> loginToAccount()
            is SettingsEvent.LogoutPressed -> logout()
            is SettingsEvent.SharePressed -> openShareScreen()
        }
    }

    private fun editNickname() {
        // TODO("Not yet implemented")
    }

    private fun openShareScreen() {
        // TODO("Not yet implemented")
    }

    private fun logout() {
        // TODO("Not yet implemented")
    }

    private fun loginToAccount() {
        // TODO("Not yet implemented")
    }

    private fun openBlackList() {
        // TODO("Not yet implemented")
    }

    fun saveNickname(email: String, nickname: String, callback: (Nickname) -> Unit) {

    }

    enum class Nickname {
        Success,
        Empty,
        Saved,
        Busy,
        Error
    }
}
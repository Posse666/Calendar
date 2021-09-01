package com.posse.kotlin1.calendar.viewModel

import androidx.annotation.StyleRes
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.posse.kotlin1.calendar.app.App
import com.posse.kotlin1.calendar.model.repository.Repository
import com.posse.kotlin1.calendar.model.repository.RepositoryFirestoreImpl
import com.posse.kotlin1.calendar.utils.*

class SettingsViewModel : ViewModel() {
    private val repository: Repository = RepositoryFirestoreImpl.newInstance()
    private val lastTheme: MutableLiveData<Int> = MutableLiveData(
        if (App.sharedPreferences?.lightTheme == true) {
            THEME.DAY.themeID
        } else {
            THEME.NIGHT.themeID
        }
    )

    var switchState: Boolean
        get() = App.sharedPreferences?.themeSwitch ?: true
        set(value) {
            App.sharedPreferences?.themeSwitch = value
        }

    var lightTheme: Boolean
        get() = App.sharedPreferences?.lightTheme ?: true
        set(value) {
            App.sharedPreferences?.lightTheme = value
            switchTheme()
        }

    fun getLastTheme() = lastTheme

    fun saveNickname(email: String, nickname: String, callback: (Boolean?) -> Unit) {
        repository.getNicknames { users ->
            when {
                users == null -> callback.invoke(null)
                nickname == "" -> callback.invoke(false)
                else -> {
                    users.forEach {
                        if (it.value.lowercase() == nickname.lowercase() && it.key != email) {
                            callback.invoke(false)
                            return@getNicknames
                        }
                    }
                    App.sharedPreferences?.nickName = nickname
                    repository.saveNickname(email, nickname)
                    callback.invoke(true)
                }
            }
        }
    }

    private fun switchTheme() {
        if (App.sharedPreferences?.lightTheme == true) changeTheme(THEME.DAY.themeID)
        else changeTheme(THEME.NIGHT.themeID)
    }

    private fun changeTheme(@StyleRes theme: Int) {
        if (theme != lastTheme.value) {
            lastTheme.value = theme
        }
    }
}
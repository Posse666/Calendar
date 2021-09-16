package com.posse.kotlin1.calendar.viewModel

import androidx.annotation.StyleRes
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.posse.kotlin1.calendar.app.App
import com.posse.kotlin1.calendar.model.User
import com.posse.kotlin1.calendar.model.repository.COLLECTION_USERS
import com.posse.kotlin1.calendar.model.repository.DOCUMENTS
import com.posse.kotlin1.calendar.model.repository.Repository
import com.posse.kotlin1.calendar.model.repository.RepositoryFirestoreImpl
import com.posse.kotlin1.calendar.utils.*
import java.util.*

class SettingsViewModel : ViewModel() {
    private val repository: Repository = RepositoryFirestoreImpl.newInstance()
    private val lastTheme: MutableLiveData<Int> = MutableLiveData(
        if (App.sharedPreferences.lightTheme) {
            THEME.DAY.themeID
        } else {
            THEME.NIGHT.themeID
        }
    )

    var switchState: Boolean
        get() = App.sharedPreferences.themeSwitch
        set(value) {
            App.sharedPreferences.themeSwitch = value
        }

    var lightTheme: Boolean
        get() = App.sharedPreferences.lightTheme
        set(value) {
            App.sharedPreferences.lightTheme = value
            switchTheme(value)
        }

    fun getLastTheme() = lastTheme

    fun saveNickname(email: String, nickname: String, callback: (NICKNAME) -> Unit) {
        repository.getData(DOCUMENTS.USERS, COLLECTION_USERS) { users, _ ->
            when (users) {
                null -> callback.invoke(NICKNAME.EMPTY)
                else -> {
                    users.forEach { userMap ->
                        try {
                            val user = (userMap.value as Map<String, Any>).toDataClass<User>()
                            if ((user.nickname).lowercase() == nickname.lowercase() && user.email != email) {
                                callback.invoke(NICKNAME.BUSY)
                                return@getData
                            }
                        } catch (e: Exception) {
                            callback.invoke(NICKNAME.ERROR)
                            return@getData
                        }
                    }
                    App.sharedPreferences.nickName = nickname
                    App.sharedPreferences.token?.let {
                        repository.saveUser(User(email, nickname, getStringLocale(), it))
                    }
                    callback.invoke(NICKNAME.SAVED)
                }
            }
        }
    }

    private fun switchTheme(day: Boolean) {
        if (day) changeTheme(THEME.DAY.themeID)
        else changeTheme(THEME.NIGHT.themeID)
    }

    private fun changeTheme(@StyleRes theme: Int) {
        if (theme != lastTheme.value) {
            lastTheme.value = theme
        }
    }
}

enum class NICKNAME {
    EMPTY,
    SAVED,
    BUSY,
    ERROR
}
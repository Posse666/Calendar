package com.posse.kotlin1.calendar.viewModel

import android.content.SharedPreferences
import androidx.annotation.StyleRes
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.posse.kotlin1.calendar.model.User
import com.posse.kotlin1.calendar.model.repository.Documents
import com.posse.kotlin1.calendar.model.repository.Repository
import com.posse.kotlin1.calendar.model.repository.RepositoryFirestoreImpl.Companion.COLLECTION_USERS
import com.posse.kotlin1.calendar.utils.*
import javax.inject.Inject

class SettingsViewModel @Inject constructor(
    private val repository: Repository,
    private val sharedPreferences: SharedPreferences
) : ViewModel() {
    private val lastTheme: MutableLiveData<Int> = MutableLiveData(
        if (sharedPreferences.lightTheme) {
            ThemeUtils.THEME.DAY.themeID
        } else {
            ThemeUtils.THEME.NIGHT.themeID
        }
    )

    var switchState: Boolean
        get() = sharedPreferences.themeSwitch
        set(value) {
            sharedPreferences.themeSwitch = value
        }

    var lightTheme: Boolean
        get() = sharedPreferences.lightTheme
        set(value) {
            sharedPreferences.lightTheme = value
            switchTheme(value)
        }

    fun getLastTheme() = lastTheme

    fun saveNickname(email: String, nickname: String, callback: (Nickname) -> Unit) {
        repository.getData(Documents.Users, COLLECTION_USERS) { users, _ ->
            when (users) {
                null -> callback(Nickname.Empty)
                else -> {
                    users.forEach { userMap ->
                        try {
                            @Suppress("UNCHECKED_CAST")
                            val user = (userMap.value as Map<String, Any>).toDataClass<User>()
                            if ((user.nickname).lowercase() == nickname.lowercase() && user.email != email) {
                                callback(Nickname.Busy)
                                return@getData
                            }
                        } catch (e: Exception) {
                            callback(Nickname.Error)
                            return@getData
                        }
                    }
                    sharedPreferences.nickName = nickname
                    sharedPreferences.token?.let {
                        repository.saveUser(User(email, nickname, it))
                    }
                    callback(Nickname.Saved)
                }
            }
        }
    }

    private fun switchTheme(day: Boolean) {
        if (day) changeTheme(ThemeUtils.THEME.DAY.themeID)
        else changeTheme(ThemeUtils.THEME.NIGHT.themeID)
    }

    private fun changeTheme(@StyleRes theme: Int) {
        if (theme != lastTheme.value) {
            lastTheme.value = theme
        }
    }

    enum class Nickname {
        Empty,
        Saved,
        Busy,
        Error
    }
}
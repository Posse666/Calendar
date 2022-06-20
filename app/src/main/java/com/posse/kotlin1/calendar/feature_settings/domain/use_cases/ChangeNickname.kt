package com.posse.kotlin1.calendar.feature_settings.domain.use_cases

import com.posse.kotlin1.calendar.common.data.model.Documents
import com.posse.kotlin1.calendar.common.data.model.User
import com.posse.kotlin1.calendar.common.data.utils.toDataClass
import com.posse.kotlin1.calendar.feature_settings.presentation.SettingsViewModel
import com.posse.kotlin1.calendar.model.repository.RepositoryFirestoreImpl
import com.posse.kotlin1.calendar.utils.nickName
import com.posse.kotlin1.calendar.utils.token

class ChangeNickname {
    suspend operator fun invoke(newName: String): SettingsViewModel.Nickname {
        repository.getData(Documents.Users, RepositoryFirestoreImpl.COLLECTION_USERS) { users, _ ->
            when (users) {
                null -> callback(SettingsViewModel.Nickname.Empty)
                else -> {
                    users.forEach { userMap ->
                        try {
                            @Suppress("UNCHECKED_CAST")
                            val user = (userMap.value as Map<String, Any>).toDataClass<User>()
                            if ((user.nickname).lowercase() == nickname.lowercase() && user.email != email) {
                                callback(SettingsViewModel.Nickname.Busy)
                                return@getData
                            }
                        } catch (e: Exception) {
                            callback(SettingsViewModel.Nickname.Error)
                            return@getData
                        }
                    }
                    sharedPreferences.nickName = nickname
                    sharedPreferences.token?.let {
                        repository.saveUser(User(email, nickname, locale.getStringLocale(), it))
                    }
                    callback(SettingsViewModel.Nickname.Saved)
                }
            }
        }
    }
}
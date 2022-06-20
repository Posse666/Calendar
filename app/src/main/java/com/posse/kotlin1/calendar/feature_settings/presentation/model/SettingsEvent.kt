package com.posse.kotlin1.calendar.feature_settings.presentation.model

sealed class SettingsEvent {
    object LoginPressed : SettingsEvent()
    object LogoutPressed : SettingsEvent()
    object BlackListPressed : SettingsEvent()
    object SharePressed : SettingsEvent()
    object EditNicknamePressed : SettingsEvent()
}

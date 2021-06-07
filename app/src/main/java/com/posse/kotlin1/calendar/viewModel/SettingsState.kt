package com.posse.kotlin1.calendar.viewModel

import android.net.Uri

sealed class SettingsState {
    data class LoggedIn(
        val userPicture: Uri?,
        val userEmail: String?
    ) : SettingsState()

    object LoggedOut : SettingsState()
}
package com.posse.kotlin1.calendar.viewModel

import android.net.Uri

sealed class AccountState {
    data class LoggedIn(
        val userPicture: Uri?,
        val userEmail: String?
    ) : AccountState()

    object LoggedOut : AccountState()
}
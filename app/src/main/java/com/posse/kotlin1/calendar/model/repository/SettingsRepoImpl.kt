package com.posse.kotlin1.calendar.model.repository

import android.content.Context
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.posse.kotlin1.calendar.viewModel.SettingsState

class SettingsRepoImpl : SettingsRepo {
    override fun getSettingsState(context: Context): SettingsState {
        val googleAccount = GoogleSignIn.getLastSignedInAccount(context)
        return if (googleAccount != null) {
            SettingsState.LoggedIn(googleAccount.photoUrl, googleAccount.email)
        } else {
            SettingsState.LoggedOut
        }
    }
}
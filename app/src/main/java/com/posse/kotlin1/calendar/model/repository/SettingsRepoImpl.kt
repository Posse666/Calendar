package com.posse.kotlin1.calendar.model.repository

import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.posse.kotlin1.calendar.app.App
import com.posse.kotlin1.calendar.viewModel.SettingsState

class SettingsRepoImpl : SettingsRepo {
    override fun getSettingsState(): SettingsState {
        val googleAccount = GoogleSignIn.getLastSignedInAccount(App.appInstance)
        return if (googleAccount != null) {
            SettingsState.LoggedIn(googleAccount.photoUrl, googleAccount.email)
        } else {
            SettingsState.LoggedOut
        }
    }
}
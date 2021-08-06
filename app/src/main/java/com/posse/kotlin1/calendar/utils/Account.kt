package com.posse.kotlin1.calendar.utils

import android.app.Activity
import android.util.Log
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.posse.kotlin1.calendar.app.App
import com.posse.kotlin1.calendar.viewModel.AccountState

object Account {
    private val liveData: MutableLiveData<AccountState> = MutableLiveData()
    private var googleAccount: GoogleSignInAccount? = null
    private val gso = GoogleSignInOptions
        .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestEmail()
        .build()

    fun getLiveData() = liveData

    private fun setAuthResult(result: ActivityResult) {
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                googleAccount = task.getResult(ApiException::class.java)
            } catch (e: ApiException) {
                Log.w("login", "signInResult:failed code=" + e.statusCode)
            }
            getAccountState()
        }
    }

    fun getAccountState() {
        googleAccount = GoogleSignIn.getLastSignedInAccount(App.appInstance)
        liveData.value = googleAccount?.let {
            AccountState.LoggedIn(it.photoUrl, it.email)
        } ?: AccountState.LoggedOut
    }

    fun login(fragment: Fragment) {
        val startLogin =
            fragment.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
                setAuthResult(result)
            }
        val googleSignInClient = GoogleSignIn.getClient(fragment.requireActivity(), gso)
        startLogin.launch(googleSignInClient.signInIntent)
    }

    fun logout(fragment: Fragment) {
        val googleSignInClient = GoogleSignIn.getClient(fragment.requireActivity(), gso)
        googleSignInClient.signOut()
        getAccountState()
    }

    fun getEmail(): String {
        return googleAccount?.email ?: ""
    }
}
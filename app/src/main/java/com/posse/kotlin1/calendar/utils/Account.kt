package com.posse.kotlin1.calendar.utils

import android.app.Activity
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.posse.kotlin1.calendar.R
import com.posse.kotlin1.calendar.app.App
import com.posse.kotlin1.calendar.model.repository.Repository
import com.posse.kotlin1.calendar.model.repository.RepositoryFirestoreImpl
import com.posse.kotlin1.calendar.viewModel.AccountState

object Account {
    private val repository: Repository = RepositoryFirestoreImpl
    private val liveData: MutableLiveData<AccountState> = MutableLiveData()
    private var googleAccount: GoogleSignInAccount? =
        GoogleSignIn.getLastSignedInAccount(App.appInstance)
    private val gso = GoogleSignInOptions
        .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken(App.appInstance?.getString(R.string.default_web_client_id))
        .requestEmail()
        .build()

    fun getLiveData() = liveData

    fun setAuthResult(result: ActivityResult) {
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val oldMail = getAvailableMail()
                googleAccount = task.getResult(ApiException::class.java)
                googleAccount?.email?.let {
                    if (oldMail != null && oldMail != it) {
                        repository.changeEmail(oldMail, it)
                    }
                }
            } catch (e: ApiException) {
                Log.w("login", "signInResult:failed code=" + e.statusCode)
            }
            authToFirestore()
            getAccountState()
        }
    }

    private fun authToFirestore() {
        FirebaseAuth.getInstance().currentUser?.delete()
        val credential = GoogleAuthProvider.getCredential(googleAccount?.idToken, null)
        FirebaseAuth.getInstance().signInWithCredential(credential)
            .addOnCompleteListener { task1: Task<AuthResult?> ->
                if (task1.isSuccessful) {
                    Log.d("login", "signInWithCredential:success")
                }
            }
    }

    fun getAccountState() {
        liveData.value = googleAccount?.let {
            AccountState.LoggedIn(it.photoUrl, it.email)
        } ?: AccountState.LoggedOut
    }

    fun login(fragment: Fragment, startLogin: ActivityResultLauncher<Intent>) {
        val googleSignInClient = GoogleSignIn.getClient(fragment.requireActivity(), gso)
        startLogin.launch(googleSignInClient.signInIntent)
    }

    fun logout(fragment: Fragment) {
        val googleSignInClient = GoogleSignIn.getClient(fragment.requireActivity(), gso)
        googleSignInClient.signOut()
        googleAccount = null
        anonymousLogin { getAccountState() }
    }

    fun getEmail(callback: (String) -> Unit) {
        val email = getAvailableMail()
        if (email == null) anonymousLogin(callback)
        else callback.invoke(email)
    }

    private fun anonymousLogin(callback: (String) -> Unit) {
        FirebaseAuth.getInstance().signInAnonymously().addOnCompleteListener {
            if (it.isSuccessful) {
                Log.d("TAG", "signInAnonymously:success")
                val email = getAvailableMail()
                callback.invoke(email!!)
            } else {
                Log.w("TAG", "signInAnonymously:failure", it.exception)
                showErrorAuthentication()
            }
        }
    }

    private fun showErrorAuthentication() {
        Toast.makeText(
            App.appInstance,
            App.appInstance?.getString(R.string.authentication_failed),
            Toast.LENGTH_SHORT
        )
            .show()
    }

    private fun getAvailableMail(): String? {
        var email = googleAccount?.email ?: FirebaseAuth.getInstance().currentUser?.email
        if (email == "" || email == null) email = FirebaseAuth.getInstance().currentUser?.uid
        return email
    }
}
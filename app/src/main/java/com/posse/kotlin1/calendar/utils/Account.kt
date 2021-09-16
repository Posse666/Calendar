package com.posse.kotlin1.calendar.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.posse.kotlin1.calendar.R
import com.posse.kotlin1.calendar.app.App
import com.posse.kotlin1.calendar.model.User
import com.posse.kotlin1.calendar.model.repository.COLLECTION_USERS
import com.posse.kotlin1.calendar.model.repository.DOCUMENTS
import com.posse.kotlin1.calendar.model.repository.Repository
import com.posse.kotlin1.calendar.model.repository.RepositoryFirestoreImpl

object Account {
    private val repository: Repository = RepositoryFirestoreImpl.newInstance()
    private val liveData: MutableLiveData<AccountState> = MutableLiveData()
    private lateinit var oldEmail: String
    private var googleAccount: GoogleSignInAccount? =
        GoogleSignIn.getLastSignedInAccount(App.appInstance as Context)
    private val gso = GoogleSignInOptions
        .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken(App.appInstance.getString(R.string.default_web_client_id))
        .requestEmail()
        .build()

    fun getLiveData() = liveData

    fun setAuthResult(result: ActivityResult, callback: () -> Unit) {
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                googleAccount = task.getResult(ApiException::class.java)
            } catch (e: ApiException) {
                Log.w("login", "signInResult:failed code=" + e.statusCode)
            }
            authToFirestore(callback)
        }
    }

    private fun authToFirestore(callback: () -> Unit) {
        FirebaseAuth.getInstance().currentUser?.delete()
        val credential = GoogleAuthProvider.getCredential(googleAccount?.idToken, null)
        FirebaseAuth.getInstance().signInWithCredential(credential)
            .addOnSuccessListener {
                googleAccount?.email?.let { email ->
                    var nickName = App.sharedPreferences.nickName ?: email
                    repository.getData(DOCUMENTS.USERS, COLLECTION_USERS) { users, _ ->
                        users?.forEach { userMap ->
                            try {
                                val user = (userMap.value as Map<String, Any>).toDataClass<User>()
                                if (user.email == email) nickName = user.nickname
                            } catch (e: Exception) {
                                callback.invoke()
                            }
                        }
                        App.sharedPreferences.nickName = nickName
                        repository.mergeDates(oldEmail, email, nickName)
                        getAccountState()
                        Log.d("login", "signInWithCredential:success")
                    }
                }
            }
    }

    fun getAccountState() {
        liveData.value = googleAccount?.let {
            AccountState.LoggedIn(it.photoUrl, it.email!!, App.sharedPreferences.nickName!!)
        } ?: AccountState.LoggedOut
    }

    fun login(fragment: Fragment, startLogin: ActivityResultLauncher<Intent>) {
        if (isNetworkOnline()) {
            oldEmail = getEmail()!!
            val googleSignInClient = GoogleSignIn.getClient(fragment.requireActivity(), gso)
            startLogin.launch(googleSignInClient.signInIntent)
        } else fragment.context?.showToast(App.appInstance.getString(R.string.network_offline))
    }

    fun logout(fragment: Fragment) {
        if (isNetworkOnline()) {
            val googleSignInClient = GoogleSignIn.getClient(fragment.requireActivity(), gso)
            googleSignInClient.signOut()
            googleAccount = null
            App.sharedPreferences.nickName = null
            anonymousLogin { getAccountState() }
        } else fragment.context?.showToast(App.appInstance.getString(R.string.network_offline))
    }

    fun getEmail(): String? {
        var email = googleAccount?.email ?: FirebaseAuth.getInstance().currentUser?.email
        if (email == "" || email == null) email = FirebaseAuth.getInstance().currentUser?.uid
        return email
    }

    fun anonymousLogin(callback: (String) -> Unit) {
        FirebaseAuth.getInstance().signInAnonymously().addOnCompleteListener {
            if (it.isSuccessful) {
                Log.d("TAG", "signInAnonymously:success")
                val email = it.result?.user?.uid!!
                oldEmail = email
                callback.invoke(email)
            } else {
                Log.w("TAG", "signInAnonymously:failure", it.exception)
                App.appInstance.showToast(App.appInstance.getString(R.string.network_offline))
            }
        }
    }
}

sealed class AccountState {
    data class LoggedIn(
        val userPicture: Uri?,
        val userEmail: String,
        val nickname: String
    ) : AccountState()

    object LoggedOut : AccountState()
}
package com.posse.kotlin1.calendar.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
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
import com.posse.kotlin1.calendar.common.data.model.User
import com.posse.kotlin1.calendar.common.data.model.Documents
import com.posse.kotlin1.calendar.common.data.utils.toDataClass
import com.posse.kotlin1.calendar.common.domain.utils.NetworkStatus
import com.posse.kotlin1.calendar.model.repository.Repository
import com.posse.kotlin1.calendar.model.repository.RepositoryFirestoreImpl.Companion.COLLECTION_USERS
import javax.inject.Inject

class Account @Inject constructor(
    private val repository: Repository,
    private val stringProvider: StringProvider,
    private val sharedPreferences: SharedPreferences,
    private val networkStatus: NetworkStatus,
    private var googleAccount: GoogleSignInAccount?,
    private val gso: GoogleSignInOptions
) {
    private val liveData: MutableLiveData<AccountState> = MutableLiveData()
    private lateinit var oldEmail: String

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
                    var nickName = sharedPreferences.nickName ?: email
                    repository.getData(Documents.Users, COLLECTION_USERS) { users, _ ->
                        users?.forEach { userMap ->
                            try {
                                @Suppress("UNCHECKED_CAST")
                                val user = (userMap.value as Map<String, Any>).toDataClass<User>()
                                if (user.email == email) nickName = user.nickname
                            } catch (e: Exception) {
                                callback.invoke()
                            }
                        }
                        sharedPreferences.nickName = nickName
                        repository.mergeDates(oldEmail, email, nickName)
                        getAccountState()
                        Log.d("login", "signInWithCredential:success")
                    }
                }
            }
    }

    fun getAccountState() {
        liveData.value = googleAccount?.let {
            AccountState.LoggedIn(it.photoUrl, it.email!!, sharedPreferences.nickName!!)
        } ?: AccountState.LoggedOut
    }

    fun login(fragment: Fragment, startLogin: ActivityResultLauncher<Intent>) {
        if (networkStatus.isNetworkOnline()) {
            oldEmail = getEmail()!!
            val googleSignInClient = GoogleSignIn.getClient(fragment.requireActivity(), gso)
            startLogin.launch(googleSignInClient.signInIntent)
        } else fragment.context?.showToast(stringProvider.getString(R.string.network_offline))
    }

    fun logout(fragment: Fragment) {
        if (networkStatus.isNetworkOnline()) {
            val googleSignInClient = GoogleSignIn.getClient(fragment.requireActivity(), gso)
            googleSignInClient.signOut()
            googleAccount = null
            sharedPreferences.nickName = null
            anonymousLogin(fragment.requireContext()) { getAccountState() }
        } else fragment.context?.showToast(stringProvider.getString(R.string.network_offline))
    }

    fun getEmail(): String? {
        var email = googleAccount?.email ?: FirebaseAuth.getInstance().currentUser?.email
        if (email == "" || email == null) email = FirebaseAuth.getInstance().currentUser?.uid
        return email
    }

    fun anonymousLogin(context: Context, callback: (String) -> Unit) {
        FirebaseAuth.getInstance().signInAnonymously().addOnCompleteListener {
            if (it.isSuccessful) {
                Log.d("TAG", "signInAnonymously:success")
                val email = it.result?.user?.uid!!
                oldEmail = email
                callback(email)
            } else {
                Log.w("TAG", "signInAnonymously:failure", it.exception)
                context.showToast(stringProvider.getString(R.string.network_offline))
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
package com.posse.kotlin1.calendar.common.data.repository

import android.content.SharedPreferences
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.FirebaseAuth
import com.posse.kotlin1.calendar.common.domain.model.User
import com.posse.kotlin1.calendar.common.domain.repository.AccountRepository
import com.posse.kotlin1.calendar.utils.nickName
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject

class AccountRepositoryImpl @Inject constructor(
    private var googleAccount: GoogleSignInAccount?,
    private val sharedPreferences: SharedPreferences
) : AccountRepository {

    override suspend fun getMyMail(): String? {
        return coroutineScope {
            val firebaseAuth = FirebaseAuth.getInstance()
            var email = googleAccount?.email ?: firebaseAuth.currentUser?.email
            if (email == "" || email == null) email = firebaseAuth.currentUser?.uid
            email
        }
    }

    override fun getCurrentUser(): User? {
        return googleAccount?.let { account ->
            account.email?.let { mail ->
                User(
                    nickName = sharedPreferences.nickName ?: mail,
                    email = mail,
                    picture = account.photoUrl
                )
            }
        }
    }


}
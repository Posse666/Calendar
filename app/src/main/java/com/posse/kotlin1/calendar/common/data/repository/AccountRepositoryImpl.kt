package com.posse.kotlin1.calendar.common.data.repository

import android.content.SharedPreferences
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.FirebaseAuth
import com.posse.kotlin1.calendar.common.domain.model.User
import com.posse.kotlin1.calendar.common.domain.repository.AccountRepository
import com.posse.kotlin1.calendar.utils.nickName
import javax.inject.Inject

class AccountRepositoryImpl @Inject constructor(
    private var googleAccount: GoogleSignInAccount?,
    private val sharedPreferences: SharedPreferences
) : AccountRepository {

    override fun getMyEmailOrId(): String {
        val firebaseAuth = FirebaseAuth.getInstance()
        return googleAccount?.email
            ?: firebaseAuth.currentUser?.email
            ?: firebaseAuth.currentUser?.uid
            ?: throw RuntimeException("User email or Id not found")
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
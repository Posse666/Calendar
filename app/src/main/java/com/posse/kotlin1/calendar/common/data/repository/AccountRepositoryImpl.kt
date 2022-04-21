package com.posse.kotlin1.calendar.common.data.repository

import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.FirebaseAuth
import com.posse.kotlin1.calendar.common.domain.repository.AccountRepository
import kotlinx.coroutines.coroutineScope

class AccountRepositoryImpl(
    private var googleAccount: GoogleSignInAccount?
) : AccountRepository {

    override suspend fun getMyMail(): String? {
        return coroutineScope {
            val firebaseAuth = FirebaseAuth.getInstance()
            var email = googleAccount?.email ?: firebaseAuth.currentUser?.email
            if (email == "" || email == null) email = firebaseAuth.currentUser?.uid
            email
        }
    }
}
package com.posse.kotlin1.calendar.di.modules

import android.content.Context
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.posse.kotlin1.calendar.R
import com.posse.kotlin1.calendar.utils.StringProvider
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class GoogleModule {

    @Provides
    fun provideGoogleSignInAccount(context: Context): GoogleSignInAccount? =
        GoogleSignIn.getLastSignedInAccount(context)

    @Provides
    @Singleton
    fun provideGoogleSignInOptions(stringProvider: StringProvider): GoogleSignInOptions =
        GoogleSignInOptions
            .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(stringProvider.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
}
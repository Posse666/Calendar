package com.posse.kotlin1.calendar.di.modules

import android.content.Context
import android.content.SharedPreferences
import com.posse.kotlin1.calendar.model.repository.Repository
import com.posse.kotlin1.calendar.utils.Account
import com.posse.kotlin1.calendar.utils.NetworkStatus
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AccountModule {

    @Singleton
    @Provides
    fun account(
        repository: Repository,
        context: Context,
        sharedPreferences: SharedPreferences,
        networkStatus: NetworkStatus
    ): Account = Account(repository, context, sharedPreferences, networkStatus)
}
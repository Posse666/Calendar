package com.posse.kotlin1.calendar.di.modules

import android.content.SharedPreferences
import com.posse.kotlin1.calendar.model.repository.Repository
import com.posse.kotlin1.calendar.model.repository.RepositoryFirestoreImpl
import com.posse.kotlin1.calendar.utils.LocaleUtils
import com.posse.kotlin1.calendar.utils.NetworkStatus
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class DataSourceModule {

    @Provides
    @Singleton
    internal fun provideRemoteDataSource(
        sharedPreferences: SharedPreferences,
        networkStatus: NetworkStatus,
        localeUtils: LocaleUtils
    ): Repository = RepositoryFirestoreImpl(sharedPreferences, networkStatus, localeUtils)

}
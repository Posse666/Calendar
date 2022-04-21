package com.posse.kotlin1.calendar.di.modules

import android.content.Context
import com.posse.kotlin1.calendar.common.domain.utils.NetworkStatus
import com.posse.kotlin1.calendar.common.domain.utils.NetworkStatusImpl
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Suppress("FunctionName")
@Module
class NetworkModule {

    @Provides
    @Singleton
    fun provideNetworkStatus(context: Context): NetworkStatus {
        return NetworkStatusImpl(context)
    }
}
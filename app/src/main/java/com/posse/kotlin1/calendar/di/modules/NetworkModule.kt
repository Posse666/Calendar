package com.posse.kotlin1.calendar.di.modules

import android.content.Context
import com.posse.kotlin1.calendar.utils.NetworkStatus
import com.posse.kotlin1.calendar.utils.NetworkStatusImpl
import dagger.Module
import dagger.Provides

@Module
class NetworkModule {

    @Provides
    fun getNetworkStatus(context: Context): NetworkStatus = NetworkStatusImpl(context)
}
package com.posse.kotlin1.calendar.di.modules

import com.posse.kotlin1.calendar.utils.NetworkStatus
import com.posse.kotlin1.calendar.utils.NetworkStatusImpl
import dagger.Binds
import dagger.Module

@Suppress("FunctionName")
@Module
interface NetworkModule {

    @Binds
    fun bindNetworkStatusImpl_to_NetworkStatus(networkStatusImpl: NetworkStatusImpl): NetworkStatus
}
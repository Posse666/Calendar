package com.posse.kotlin1.calendar.di.modules

import com.posse.kotlin1.calendar.common.utils.CoroutinesDispatchers
import com.posse.kotlin1.calendar.common.utils.CoroutinesDispatchersImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface CoroutineDispatchersModule {

    @Binds
    @Singleton
    fun bindDispatchers(dispatcherProviderImpl: CoroutinesDispatchersImpl): CoroutinesDispatchers
}
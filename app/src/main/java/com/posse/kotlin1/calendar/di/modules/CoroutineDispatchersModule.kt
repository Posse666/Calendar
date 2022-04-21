package com.posse.kotlin1.calendar.di.modules

import com.posse.kotlin1.calendar.common.domain.utils.DispatcherProvider
import com.posse.kotlin1.calendar.common.domain.utils.DispatcherProviderImpl
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class CoroutineDispatchersModule {

    @Provides
    @Singleton
    fun provideDispatchers(): DispatcherProvider {
        return DispatcherProviderImpl()
    }
}
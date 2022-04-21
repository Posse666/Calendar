package com.posse.kotlin1.calendar.di.modules

import com.posse.kotlin1.calendar.common.data.repository.MessengerImpl
import com.posse.kotlin1.calendar.common.domain.repository.Messenger
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class MessengerModule {

    @Provides
    @Singleton
    fun provideMessenger(): Messenger {
        return MessengerImpl()
    }
}
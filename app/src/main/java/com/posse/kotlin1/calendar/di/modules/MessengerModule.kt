package com.posse.kotlin1.calendar.di.modules

import com.posse.kotlin1.calendar.common.data.repository.MessageHandlerImpl
import com.posse.kotlin1.calendar.common.data.repository.MessengerImpl
import com.posse.kotlin1.calendar.common.domain.repository.MessageHandler
import com.posse.kotlin1.calendar.common.domain.repository.Messenger
import dagger.Binds
import dagger.Module
import javax.inject.Singleton

@Module
interface MessengerModule {

    @Binds
    @Singleton
    fun provideMessenger(messengerImpl: MessengerImpl): Messenger

    @Binds
    @Singleton
    fun provideMessageHandler(messageHandlerImpl: MessageHandlerImpl): MessageHandler
}
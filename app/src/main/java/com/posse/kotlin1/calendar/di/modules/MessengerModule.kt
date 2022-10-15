package com.posse.kotlin1.calendar.di.modules

import com.posse.kotlin1.calendar.common.data.repository.MessageHandlerImpl
import com.posse.kotlin1.calendar.common.data.repository.MessengerImpl
import com.posse.kotlin1.calendar.common.domain.repository.MessageHandler
import com.posse.kotlin1.calendar.common.domain.repository.Messenger
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Singleton

@Module
@InstallIn(ViewModelComponent::class)
interface MessengerModule {

    @Binds
    @ViewModelScoped
    fun provideMessenger(messengerImpl: MessengerImpl): Messenger

    @Binds
    @ViewModelScoped
    fun provideMessageHandler(messageHandlerImpl: MessageHandlerImpl): MessageHandler
}
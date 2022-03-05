package com.posse.kotlin1.calendar.di.modules

import com.posse.kotlin1.calendar.firebaseMessagingService.MyFirebaseMessagingService
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
interface ServicesModule {

    @ContributesAndroidInjector
    fun fcmService(): MyFirebaseMessagingService
}
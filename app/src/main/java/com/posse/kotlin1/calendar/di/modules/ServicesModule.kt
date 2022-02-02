package com.posse.kotlin1.calendar.di.modules

import com.posse.kotlin1.calendar.firebaseMessagingService.MyFirebaseMessagingService
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ServicesModule {
    @ContributesAndroidInjector
    abstract fun fcmService(): MyFirebaseMessagingService
}
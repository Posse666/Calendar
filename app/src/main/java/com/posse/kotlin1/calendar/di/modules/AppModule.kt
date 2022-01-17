package com.posse.kotlin1.calendar.di.modules

import android.content.Context
import com.posse.kotlin1.calendar.app.App
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AppModule(private val app: App) {

    @Singleton
    @Provides
    fun app(): Context = app
}
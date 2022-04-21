package com.posse.kotlin1.calendar.di.modules

import android.content.Context
import android.content.SharedPreferences
import dagger.Module
import dagger.Provides
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

@Module
class SharedModule {

    @Provides
    @Singleton
    fun sharedProvider(@ApplicationContext context: Context): SharedPreferences =
        context.getSharedPreferences(SHARED, Context.MODE_PRIVATE)
}
package com.posse.kotlin1.calendar.di.modules

import android.content.Context
import android.content.SharedPreferences
import com.posse.kotlin1.calendar.di.modules.SHARED
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class SharedModule {

    @Provides
    @Singleton
    fun sharedProvider(context: Context): SharedPreferences =
        context.getSharedPreferences(SHARED, Context.MODE_PRIVATE)
}
package com.posse.kotlin1.calendar.di.modules

import android.content.Context
import android.content.SharedPreferences
import com.posse.kotlin1.calendar.utils.ThemeUtils
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class ThemeModule {

    @Provides
    @Singleton
    fun themeProvider(sharedPreferences: SharedPreferences, context: Context): ThemeUtils =
        ThemeUtils(sharedPreferences, context)
}
package com.posse.kotlin1.calendar.di.modules

import android.content.SharedPreferences
import com.posse.kotlin1.calendar.utils.LocaleUtils
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class LocaleModule {

    @Provides
    @Singleton
    internal fun provideLocale(sharedPreferences: SharedPreferences): LocaleUtils {
        return LocaleUtils(sharedPreferences)
    }
}
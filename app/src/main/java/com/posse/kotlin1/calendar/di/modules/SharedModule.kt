package com.posse.kotlin1.calendar.di.modules

import android.content.Context
import android.content.SharedPreferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
class SharedModule {

    @Provides
    @ViewModelScoped
    fun sharedProvider(@ApplicationContext context: Context): SharedPreferences =
        context.getSharedPreferences(SHARED, Context.MODE_PRIVATE)
}
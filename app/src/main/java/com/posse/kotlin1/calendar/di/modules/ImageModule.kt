package com.posse.kotlin1.calendar.di.modules

import com.squareup.picasso.Picasso
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class ImageModule {

    @Provides
    @Singleton
    fun providePicasso(): Picasso = Picasso.get()
}
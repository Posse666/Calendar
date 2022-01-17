package com.posse.kotlin1.calendar.di.component

import android.app.Application
import com.posse.kotlin1.calendar.app.App
import com.posse.kotlin1.calendar.di.modules.*
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

@Component(
    modules = [
        ActivityModule::class,
        FragmentModule::class,
        ViewModelModule::class,
        AndroidSupportInjectionModule::class,
        AndroidInjectionModule::class,
        AppModule::class,
        ImageModule::class,
        SharedModule::class,
        StringProviderModule::class,
        AccountModule::class,
        DataSourceModule::class,
        LocaleModule::class,
        NetworkModule::class,
        ThemeModule::class
    ]
)

@Singleton
interface AppComponent {

    @Component.Builder
    interface Builder {

        @BindsInstance
        fun application(app: Application): Builder

        fun appModule(appModule: AppModule): Builder

        fun build(): AppComponent
    }

    fun inject(app: App)
}
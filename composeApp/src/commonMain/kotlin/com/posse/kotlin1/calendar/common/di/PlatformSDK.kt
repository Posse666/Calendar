package com.posse.kotlin1.calendar.common.di

import com.posse.kotlin1.calendar.feature_calendar.di.calendarModule
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.direct
import org.kodein.di.singleton

object PlatformSDK {
    fun init(configuration: PlatformConfiguration) {

        val coreModule = DI.Module("coreModule") {
            bind<PlatformConfiguration>() with singleton { configuration }
        }

        Inject.createDependencies(
            DI {
                importAll(
                    coreModule,
                    commonModule,
                    calendarModule
                )
            }.direct
        )
    }
}
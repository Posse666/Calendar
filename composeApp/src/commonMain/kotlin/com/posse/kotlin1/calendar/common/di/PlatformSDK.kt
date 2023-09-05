package com.posse.kotlin1.calendar.common.di

import com.posse.kotlin1.calendar.common.utils.CoroutineDispatchers
import com.posse.kotlin1.calendar.common.utils.CoroutineDispatchersImpl
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.direct
import org.kodein.di.singleton

object PlatformSDK {
    fun init(configuration: PlatformConfiguration) {
        Inject.createDependencies(
            DI {
                DI.Module("umbrellaModule") {
                    bind<PlatformConfiguration>() with singleton { configuration }
                    bind<CoroutineDispatchers>() with singleton { CoroutineDispatchersImpl() }
                }
            }.direct
        )
    }
}
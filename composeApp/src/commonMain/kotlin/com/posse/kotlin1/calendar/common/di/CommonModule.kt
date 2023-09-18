package com.posse.kotlin1.calendar.common.di

import com.posse.kotlin1.calendar.common.data.repository.MessageHandlerImpl
import com.posse.kotlin1.calendar.common.domain.repository.MessageHandler
import com.posse.kotlin1.calendar.common.utils.CoroutineDispatchers
import com.posse.kotlin1.calendar.common.utils.CoroutineDispatchersImpl
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.singleton

val commonModule = DI.Module("CommonModule") {
    bind<CoroutineDispatchers>() with singleton { CoroutineDispatchersImpl() }
    bind<MessageHandler>() with singleton {
        MessageHandlerImpl(
            coroutineDispatchers = Inject.instance()
        )
    }
}
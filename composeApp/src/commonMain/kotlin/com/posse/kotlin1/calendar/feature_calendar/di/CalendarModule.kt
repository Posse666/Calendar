package com.posse.kotlin1.calendar.feature_calendar.di

import com.posse.kotlin1.calendar.common.di.Inject
import com.posse.kotlin1.calendar.feature_calendar.data.repository.DatesRepositoryImpl
import com.posse.kotlin1.calendar.feature_calendar.domain.repository.DatesRepository
import com.posse.kotlin1.calendar.feature_calendar.domain.use_cases.CalendarUseCases
import com.posse.kotlin1.calendar.feature_calendar.domain.use_cases.DeleteDate
import com.posse.kotlin1.calendar.feature_calendar.domain.use_cases.GetCalendarData
import com.posse.kotlin1.calendar.feature_calendar.domain.use_cases.GetDates
import com.posse.kotlin1.calendar.feature_calendar.domain.use_cases.SendMessage
import com.posse.kotlin1.calendar.feature_calendar.domain.use_cases.SetDate
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.provider
import org.kodein.di.singleton

val calendarModule = DI.Module("calendarModule") {
    bind<CalendarUseCases>() with provider {
        CalendarUseCases(
            getCalendarData = GetCalendarData(),
            getDates = GetDates(repository = Inject.instance()),
            setDate = SetDate(repository = Inject.instance()),
            deleteDate = DeleteDate(repository = Inject.instance())
        )
    }

    bind<DatesRepository>() with provider {
        DatesRepositoryImpl(
            coroutineDispatchers = Inject.instance()
        )
    }

    bind<SendMessage>() with singleton { SendMessage(messageHandler = Inject.instance()) }
}
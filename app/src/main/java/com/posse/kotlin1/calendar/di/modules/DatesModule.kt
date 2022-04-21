package com.posse.kotlin1.calendar.di.modules

import com.posse.kotlin1.calendar.common.domain.repository.Messenger
import com.posse.kotlin1.calendar.common.domain.utils.NetworkStatus
import com.posse.kotlin1.calendar.feature_calendar.domain.repository.DatesRepository
import com.posse.kotlin1.calendar.feature_calendar.domain.use_case.DatesUseCases
import com.posse.kotlin1.calendar.feature_calendar.domain.use_case.DeleteDate
import com.posse.kotlin1.calendar.feature_calendar.domain.use_case.GetDates
import com.posse.kotlin1.calendar.feature_calendar.domain.use_case.SetDate
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class DatesModule {

    @Provides
    @Singleton
    fun provideDatesUseCases(
        repository: DatesRepository,
        messenger: Messenger,
        networkStatus: NetworkStatus
    ): DatesUseCases {
        return DatesUseCases(
            getDates = GetDates(repository),
            setDate = SetDate(repository, messenger, networkStatus),
            deleteDate = DeleteDate(repository)
        )
    }
}
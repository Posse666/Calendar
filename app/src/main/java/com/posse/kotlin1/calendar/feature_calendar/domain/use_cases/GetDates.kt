package com.posse.kotlin1.calendar.feature_calendar.domain.use_cases

import com.posse.kotlin1.calendar.common.domain.model.Response
import com.posse.kotlin1.calendar.feature_calendar.domain.model.DayData
import com.posse.kotlin1.calendar.feature_calendar.domain.repository.DatesRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetDates @Inject constructor(
    private val repository: DatesRepository
) {
    operator fun invoke(userMail: String): Flow<Response<List<DayData>>> {
        return repository.getDates(userMail)
    }
}
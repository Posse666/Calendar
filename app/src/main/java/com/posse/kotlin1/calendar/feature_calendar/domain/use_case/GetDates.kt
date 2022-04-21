package com.posse.kotlin1.calendar.feature_calendar.domain.use_case

import com.posse.kotlin1.calendar.common.domain.model.Response
import com.posse.kotlin1.calendar.feature_calendar.domain.model.DayData
import com.posse.kotlin1.calendar.feature_calendar.domain.repository.DatesRepository
import kotlinx.coroutines.flow.Flow

class GetDates(
    private val repository: DatesRepository
) {
    operator fun invoke(userMail: String): Flow<Response<List<DayData>>> {
        return repository.getDates(userMail)
    }
}
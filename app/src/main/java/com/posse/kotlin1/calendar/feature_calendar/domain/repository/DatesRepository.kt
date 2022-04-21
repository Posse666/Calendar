package com.posse.kotlin1.calendar.feature_calendar.domain.repository

import com.posse.kotlin1.calendar.common.domain.model.Response
import com.posse.kotlin1.calendar.feature_calendar.domain.model.DayData
import kotlinx.coroutines.flow.Flow

interface DatesRepository {
    fun getDates(userMail: String): Flow<Response<List<DayData>>>
    suspend fun changeDate(userMail: String, day: DayData, shouldDelete: Boolean): Boolean
}
package com.posse.kotlin1.calendar.feature_calendar.domain.use_cases

import com.posse.kotlin1.calendar.common.utils.CoroutinesDispatchers
import com.posse.kotlin1.calendar.feature_calendar.domain.model.MonthData
import kotlinx.coroutines.withContext
import java.time.LocalDate
import javax.inject.Inject

class CalculateMonthIndex @Inject constructor(
    private val dispatchers: CoroutinesDispatchers
) {
    suspend operator fun invoke(
        calendarData: List<MonthData>,
        date: LocalDate
    ): Int? = withContext(dispatchers.default) {
        val calendarMonth = calendarData.find { monthData ->
            monthData.weeks.flatMap { week ->
                week.map { it?.date }
            }.contains(date)
        } ?: return@withContext null

        calendarData.indexOf(calendarMonth)
    }
}
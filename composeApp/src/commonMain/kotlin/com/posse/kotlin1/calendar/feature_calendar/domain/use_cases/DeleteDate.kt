package com.posse.kotlin1.calendar.feature_calendar.domain.use_cases

import com.posse.kotlin1.calendar.feature_calendar.domain.model.DayData
import com.posse.kotlin1.calendar.feature_calendar.domain.repository.DatesRepository

class DeleteDate (
    private val repository: DatesRepository
) {
    suspend operator fun invoke(userMail: String, day: DayData): Boolean {
        return repository.changeDate(userMail, day, true)
    }
}
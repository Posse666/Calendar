package com.posse.kotlin1.calendar.feature_calendar.domain.use_case

import javax.inject.Inject

data class DatesUseCases @Inject constructor(
    val getDates: GetDates,
    val setDate: SetDate,
    val deleteDate: DeleteDate,
    val calculateStatistic: CalculateStatistic
)

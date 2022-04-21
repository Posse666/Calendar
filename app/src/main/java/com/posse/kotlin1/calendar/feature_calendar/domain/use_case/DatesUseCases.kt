package com.posse.kotlin1.calendar.feature_calendar.domain.use_case

data class DatesUseCases(
    val getDates: GetDates,
    val setDate: SetDate,
    val deleteDate: DeleteDate,
    val calculateStatistic: CalculateStatistic
)

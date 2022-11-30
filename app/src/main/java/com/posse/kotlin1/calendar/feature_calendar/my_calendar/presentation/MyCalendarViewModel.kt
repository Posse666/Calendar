package com.posse.kotlin1.calendar.feature_calendar.my_calendar.presentation

import com.posse.kotlin1.calendar.common.domain.use_case.AccountUseCases
import com.posse.kotlin1.calendar.feature_calendar.domain.use_cases.CalculateMonthIndex
import com.posse.kotlin1.calendar.feature_calendar.domain.use_cases.DatesUseCases
import com.posse.kotlin1.calendar.feature_calendar.domain.use_cases.GetCalendarData
import com.posse.kotlin1.calendar.feature_calendar.domain.use_cases.SendMessage
import com.posse.kotlin1.calendar.feature_calendar.presentation.CalendarViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MyCalendarViewModel @Inject constructor(
    getCalendarData: GetCalendarData,
    accountUseCases: AccountUseCases,
    datesUseCases: DatesUseCases,
    sendMessage: SendMessage,
    calculateMonthIndex: CalculateMonthIndex
) : CalendarViewModel(
    datesUseCases = datesUseCases,
    sendMessage = sendMessage,
    getCalendarData = getCalendarData,
    calculateMonthIndex = calculateMonthIndex,
//    email = accountUseCases.getMyMail()
    email = ""
)
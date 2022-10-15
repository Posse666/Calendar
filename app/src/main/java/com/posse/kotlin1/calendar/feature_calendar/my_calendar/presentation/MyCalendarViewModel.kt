package com.posse.kotlin1.calendar.feature_calendar.my_calendar.presentation

import com.posse.kotlin1.calendar.common.domain.use_case.AccountUseCases
import com.posse.kotlin1.calendar.feature_calendar.domain.use_case.DatesUseCases
import com.posse.kotlin1.calendar.feature_calendar.domain.use_case.GetCalendarData
import com.posse.kotlin1.calendar.feature_calendar.domain.use_case.SendMessage
import com.posse.kotlin1.calendar.feature_calendar.presentation.CalendarViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MyCalendarViewModel @Inject constructor(
    getCalendarData: GetCalendarData,
    accountUseCases: AccountUseCases,
    datesUseCases: DatesUseCases,
    sendMessage: SendMessage
) : CalendarViewModel(
    datesUseCases = datesUseCases,
    sendMessage = sendMessage,
    getCalendarData = getCalendarData,
//    email = accountUseCases.getMyMail()
    email = ""
)
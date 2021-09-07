package com.posse.kotlin1.calendar.view.calendar

import android.view.View
import com.kizitonwose.calendarview.model.CalendarDay
import com.kizitonwose.calendarview.ui.ViewContainer
import com.posse.kotlin1.calendar.databinding.CalendarDayLayoutBinding

class DayViewContainer(view: View) : ViewContainer(view) {
    val rootView = CalendarDayLayoutBinding.bind(view)
    lateinit var day: CalendarDay
}
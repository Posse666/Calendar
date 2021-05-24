package com.posse.kotlin1.calendar.view.calendar

import android.view.View
import com.kizitonwose.calendarview.model.CalendarDay
import com.kizitonwose.calendarview.ui.ViewContainer
import com.posse.kotlin1.calendar.databinding.CalendarDayLayoutBinding

class DayViewContainer(view: View) : ViewContainer(view) {
    val textView = CalendarDayLayoutBinding.bind(view).calendarDayText
    lateinit var day: CalendarDay

    init {
        view.setOnClickListener {
            // Use the CalendarDay associated with this container.
        }
    }
}
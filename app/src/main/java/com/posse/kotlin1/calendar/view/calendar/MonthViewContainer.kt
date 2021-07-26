package com.posse.kotlin1.calendar.view.calendar

import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.LinearLayoutCompat
import com.kizitonwose.calendarview.ui.ViewContainer
import com.posse.kotlin1.calendar.R
import com.posse.kotlin1.calendar.databinding.CalendarMonthHeaderLayoutBinding
import com.posse.kotlin1.calendar.utils.putText

class MonthViewContainer(view: View) : ViewContainer(view) {
    val textView = CalendarMonthHeaderLayoutBinding.bind(view).headerTextView

    init {
        val daysLayout = CalendarMonthHeaderLayoutBinding.bind(view).calendarDaysNames
        val context = view.context
        enumValues<Days>().forEach {
            val dayText = TextView(context)
            dayText.layoutParams =
                LinearLayoutCompat.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    1f
                )
            dayText.gravity = Gravity.CENTER
            dayText.textSize = 20f
            dayText.putText(context.getString(it.dayResource))
            daysLayout.addView(dayText)
        }
    }
}

enum class Month(val monthResource: Int) {
    JANUARY(R.string.january),
    FEBRUARY(R.string.february),
    MARCH(R.string.march),
    APRIL(R.string.april),
    MAY(R.string.may),
    JUNE(R.string.june),
    JULY(R.string.july),
    AUGUST(R.string.august),
    SEPTEMBER(R.string.september),
    OCTOBER(R.string.october),
    NOVEMBER(R.string.november),
    DECEMBER(R.string.december)
}

enum class Days(val dayResource: Int) {
    MONDAY(R.string.monday),
    TUESDAY(R.string.tuesday),
    WEDNESDAY(R.string.wednesday),
    THURSDAY(R.string.thursday),
    FRIDAY(R.string.friday),
    SATURDAY(R.string.saturday),
    SUNDAY(R.string.sunday)
}

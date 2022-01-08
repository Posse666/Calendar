package com.posse.kotlin1.calendar.view.calendar

import android.view.View
import androidx.core.content.res.ResourcesCompat
import com.kizitonwose.calendarview.model.CalendarDay
import com.kizitonwose.calendarview.ui.ViewContainer
import com.posse.kotlin1.calendar.R
import com.posse.kotlin1.calendar.databinding.ShotGlassLayoutBinding
import com.posse.kotlin1.calendar.utils.show
import java.time.LocalDate

class DayViewContainer(view: View) : ViewContainer(view) {
    val rootView = ShotGlassLayoutBinding.bind(view)
    lateinit var day: CalendarDay

    private fun isItToday(date: LocalDate) = date == LocalDate.now()

    private fun getColor(colorResource: Int) =
        ResourcesCompat.getColor(view.resources, colorResource, null)

    fun changeDay(
        date: LocalDate,
        drinkType: DrinkType?
    ) {
        val shotGlassFillColor: Int = when (drinkType) {
            DrinkType.Full -> R.color.fillColor
            DrinkType.Half -> R.color.halfFillColor
            else -> R.color.emptyFillColor
        }
        rootView.shotGlassInner.show()
        rootView.shotGlassInner.setColorFilter(getColor(shotGlassFillColor))

        val strokeColor = if (isItToday(date)) R.color.fillColor
        else R.color.strokeColor
        rootView.shotGlassOuter.setColorFilter(getColor(strokeColor))

        rootView.shotGlassText.show()
        val textColor = if (shotGlassFillColor != R.color.emptyFillColor) R.color.white
        else R.color.strokeColor
        rootView.shotGlassText.setTextColor(getColor(textColor))
    }
}
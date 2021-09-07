package com.posse.kotlin1.calendar.view.calendar

import android.graphics.Color
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import androidx.core.content.res.ResourcesCompat
import com.posse.kotlin1.calendar.R
import com.posse.kotlin1.calendar.app.App
import com.posse.kotlin1.calendar.databinding.CalendarDayLayoutBinding
import com.posse.kotlin1.calendar.utils.hide
import com.posse.kotlin1.calendar.utils.show
import java.time.LocalDate
import java.util.*

class Cell {

    private val cell =
        { cell: CellType, cell2: CellType, date: LocalDate ->
            if (date == LocalDate.now()) cell
            else cell2
        }

    private fun change(cellType: CellType, view: CalendarDayLayoutBinding) {
        val strokeColor: Int
        var full = false
        when (cellType) {
            CellType.EMPTY -> {
                strokeColor = getColor(R.color.strokeColor)
            }
            CellType.FULL -> {
                strokeColor = getColor(R.color.fillColor)
                full = true
            }
            CellType.SELECTED_EMPTY -> {
                strokeColor = getColor(R.color.strokeColor_selected)
            }
            CellType.SELECTED_FULL -> {
                strokeColor = getColor(R.color.strokeColor_selected)
                full = true
            }
        }
        if (full) {
            view.fillGlass.setColorFilter(getColor(R.color.fillColor))
            view.fillGlass.show()
        } else view.fillGlass.hide()
        view.mainGlass.setColorFilter(strokeColor)
    }

    private fun getColor(colorResource: Int) =
        ResourcesCompat.getColor(App.appInstance!!.resources, colorResource, null)

    fun changeDay(
        view: CalendarDayLayoutBinding,
        date: LocalDate,
        actualState: HashSet<LocalDate>
    ) {
        val textColor: Int = getTextColor(actualState.contains(date))
        val cellType: CellType = getCellType(actualState.contains(date), date)
        view.calendarDayText.setTextColor(textColor)
        change(cellType, view)
    }

    private fun getCellType(selected: Boolean, date: LocalDate): CellType {
        if (!selected) return cell(
            CellType.SELECTED_EMPTY,
            CellType.EMPTY,
            date
        )
        return cell(CellType.SELECTED_FULL, CellType.FULL, date)
    }

    private fun getTextColor(selected: Boolean): Int {
        if (selected) return Color.WHITE
        return getColor(R.color.strokeColor)
    }
}

enum class CellType {
    EMPTY,
    FULL,
    SELECTED_EMPTY,
    SELECTED_FULL
}
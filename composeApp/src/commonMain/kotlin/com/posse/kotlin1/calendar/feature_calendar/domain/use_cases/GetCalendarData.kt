package com.posse.kotlin1.calendar.feature_calendar.domain.use_cases

import com.posse.kotlin1.calendar.common.utils.DateTimeUtils
import com.posse.kotlin1.calendar.feature_calendar.domain.model.DayData
import com.posse.kotlin1.calendar.feature_calendar.domain.model.MonthData
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.Month
import kotlinx.datetime.isoDayNumber
import kotlinx.datetime.minus
import kotlinx.datetime.plus

class GetCalendarData {
    operator fun invoke(
        startDate: LocalDate = DateTimeUtils.today.minus(1, DateTimeUnit.YEAR)
    ): List<MonthData> {
        return getPeriod(startDate).map { date ->
            MonthData(
                month = date.month,
                year = date.year,
                weeks = getWeeks(date)
            )
        }
    }

    private fun getPeriod(startDate: LocalDate): List<LocalDate> {
        val period = mutableListOf<LocalDate>()
        var currentMonth = startDate
        val lastMonth = DateTimeUtils.today.plus(2, DateTimeUnit.MONTH)
        while (currentMonth.year <= lastMonth.year && currentMonth.monthNumber < lastMonth.monthNumber) {
            period.add(currentMonth)
            currentMonth = currentMonth.plus(1, DateTimeUnit.MONTH)
        }
        return period
    }

    private fun getWeeks(currentDate: LocalDate): List<List<DayData?>> {
        val year = currentDate.year
        val month = currentDate.month
        val firstDayOfMonth = LocalDate(year, month, 1)
        val calendarDays =
            (getInitialDayOfMonth(firstDayOfMonth.dayOfWeek)..month.minimumLength()).toMutableList()
        val daysToFillMonth = 7 - calendarDays.size % 7
        for (i in 0 until daysToFillMonth) {
            calendarDays.add(-1)
        }
        return calendarDays.chunked(7) { week ->
            week.map {
                if (it > 0) DayData(date = LocalDate(year, month, it))
                else null
            }
        }
    }

    private fun getInitialDayOfMonth(firstDayOfMonth: DayOfWeek): Int {
        val today = DateTimeUtils.today
        val firstDay = today.daysShift(-DayOfWeek.values().indexOf(today.dayOfWeek)).dayOfWeek
        return -((firstDayOfMonth.isoDayNumber) - (if (firstDay == DayOfWeek.MONDAY) 2 else 1))
    }

    private fun LocalDate.daysShift(days: Int): LocalDate = when {
        days < 0 -> minus(DateTimeUnit.DayBased(-days))
        days > 0 -> plus(DateTimeUnit.DayBased(days))
        else -> this
    }

    private fun Month.minimumLength(): Int {
        return when (this) {
            Month.FEBRUARY -> 28
            Month.APRIL, Month.JUNE, Month.SEPTEMBER, Month.NOVEMBER -> 30
            else -> 31
        }
    }
}
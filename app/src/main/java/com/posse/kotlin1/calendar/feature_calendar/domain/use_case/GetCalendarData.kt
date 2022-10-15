package com.posse.kotlin1.calendar.feature_calendar.domain.use_case

import com.posse.kotlin1.calendar.feature_calendar.domain.model.DayData
import com.posse.kotlin1.calendar.feature_calendar.domain.model.MonthData
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import javax.inject.Inject

class GetCalendarData @Inject constructor() {
    operator fun invoke(
        startMonth: YearMonth = YearMonth.now().minusYears(1)
    ): List<MonthData> {
        return getPeriod(startMonth).map { yearMonth ->
            MonthData(
                yearMonth = yearMonth,
                weeks = getWeeks(yearMonth)
            )
        }
    }

    private fun getPeriod(startMonth: YearMonth): List<YearMonth> {
        val period = mutableListOf<YearMonth>()
        var currentMonth = startMonth
        val lastMonth = YearMonth.now().plusMonths(2)
        while (currentMonth.isBefore(lastMonth)) {
            period.add(currentMonth)
            currentMonth = currentMonth.plusMonths(1)
        }
        return period
    }

    private fun getWeeks(currentMonth: YearMonth): List<List<DayData?>> {
        val year = currentMonth.year
        val month = currentMonth.month
        val firstDayOfMonth = LocalDate.of(year, month, 1).dayOfWeek
        val calendarDays =
            (getInitialDayOfMonth(firstDayOfMonth)..month.minLength()).toMutableList()
        val daysToFillMonth = 7 - calendarDays.size % 7
        for (i in 0 until daysToFillMonth) {
            calendarDays.add(-1)
        }
        return calendarDays.chunked(7) {
            it.map {
                if (it > 0) DayData(date = LocalDate.of(year, month, it))
                else null
            }
        }
    }

    private fun getInitialDayOfMonth(firstDayOfMonth: DayOfWeek) = -(firstDayOfMonth.value).minus(2)
}
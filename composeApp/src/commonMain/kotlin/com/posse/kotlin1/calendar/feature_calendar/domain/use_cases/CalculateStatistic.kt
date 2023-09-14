package com.posse.kotlin1.calendar.feature_calendar.domain.use_cases

import com.posse.kotlin1.calendar.common.utils.DateTimeUtils
import com.posse.kotlin1.calendar.feature_calendar.domain.model.DayData
import com.posse.kotlin1.calendar.feature_calendar.presentation.model.StatisticWithDaysState
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.minus
import kotlinx.datetime.periodUntil
import kotlinx.datetime.plus

class CalculateStatistic {
    operator fun invoke(days: Set<DayData>): StatisticWithDaysState {
        val dates = days.filter { it.drinkType != null }.map { it.date }
        return StatisticWithDaysState(
            daysOverall = getDrankDaysQuantity(dates),
            drunkRowThisYear = getDrinkMarathon(dates, THIS_YEAR),
            drunkRowOverall = getDrinkMarathon(dates, ALL_TIME),
            freshRowThisYear = getFreshMarathon(dates, THIS_YEAR),
            freshRowOverall = getFreshMarathon(dates, ALL_TIME)
        )
    }

    private fun getDrankDaysQuantity(dates: List<LocalDate>?): List<LocalDate> {
        val currentYear: Int = DateTimeUtils.today.year
        return dates
            ?.filter { it.year >= currentYear }
            ?: emptyList()
    }

    private fun getDrinkMarathon(dates: List<LocalDate>?, isThisYear: Boolean): List<LocalDate> {
        val days: MutableList<LocalDate> = mutableListOf()
        val maxDays: MutableList<LocalDate> = mutableListOf()
        val currentYear: Int = DateTimeUtils.today.year
        dates?.sorted()?.forEach {
            days.add(it)
            if (!days.contains(it.minus(1, DateTimeUnit.DAY))) {
                if (isThisYear && (it.year < currentYear || days[0].year < currentYear)) {
                    maxDays.clear()
                    val daysToDelete = mutableSetOf<LocalDate>()
                    for (day in days) {
                        if (day.year < currentYear) daysToDelete.add(day)
                    }
                    days.removeAll(daysToDelete)
                }
                if (days.size > 1) days.removeAt(days.size - 1)
                if (maxDays.size <= days.size) {
                    maxDays.clear()
                    maxDays.addAll(days)
                }
                days.clear()
                days.add(it)
                if (isThisYear && it.year < currentYear) days.clear()
            }
        }
        return if (maxDays.size > days.size) {
            maxDays
        } else {
            days
        }
    }

    private fun getFreshMarathon(dates: List<LocalDate>?, isThisYear: Boolean): List<LocalDate> {
        val datesWithCurrent = dates?.plus(DateTimeUtils.today)
        val days: MutableList<LocalDate> = mutableListOf()
        val maxDays: MutableList<LocalDate> = mutableListOf()
        var lastDate: LocalDate? = null
        val currentYear: Int = DateTimeUtils.today.year
        datesWithCurrent?.sorted()?.forEach sortedDates@{
            if (isThisYear) {
                if ((it.year >= currentYear) && (lastDate != null && lastDate!!.year < currentYear)) {
                    lastDate = LocalDate(currentYear - 1, 12, 31)
                } else {
                    lastDate = it
                    return@sortedDates
                }
            }
            lastDate?.let { lastDate ->
                val period = lastDate.periodUntil(it).days
                days.clear()
                for (i in 0 until period) {
                    days.add(lastDate.plus(i + 1, DateTimeUnit.DAY))
                }
            }
            lastDate = it
            if (maxDays.size <= days.size) {
                maxDays.clear()
                maxDays.addAll(days)
            }
        }
        return maxDays
    }

    companion object {
        private const val THIS_YEAR = true
        private const val ALL_TIME = false
    }
}
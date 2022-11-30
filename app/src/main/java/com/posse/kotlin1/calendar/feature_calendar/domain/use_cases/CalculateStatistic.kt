package com.posse.kotlin1.calendar.feature_calendar.domain.use_cases

import com.posse.kotlin1.calendar.feature_calendar.domain.model.DayData
import com.posse.kotlin1.calendar.feature_calendar.presentation.model.StatisticWithDaysState
import java.time.LocalDate
import java.time.Year
import java.time.temporal.ChronoUnit
import javax.inject.Inject

class CalculateStatistic @Inject constructor() {

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
        val currentYear: LocalDate = LocalDate.ofYearDay(Year.now().value, 1)
        return dates
            ?.filter { !it.isBefore(currentYear) }
            ?: emptyList()
    }

    private fun getDrinkMarathon(dates: List<LocalDate>?, isThisYear: Boolean): List<LocalDate> {
        val days: MutableList<LocalDate> = mutableListOf()
        val maxDays: MutableList<LocalDate> = mutableListOf()
        val currentYear: LocalDate = LocalDate.ofYearDay(Year.now().value, 1)
        dates?.sorted()?.forEach {
            days.add(it)
            if (!days.contains(it.minusDays(1))) {
                if (isThisYear && (it.isBefore(currentYear) || days[0].isBefore(currentYear))) {
                    maxDays.clear()
                    val daysToDelete = mutableSetOf<LocalDate>()
                    for (day in days) {
                        if (day.isBefore(currentYear)) daysToDelete.add(day)
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
                if (isThisYear && it.isBefore(currentYear)) days.clear()
            }
        }
        return if (maxDays.size > days.size) {
            maxDays
        } else {
            days
        }
    }

    private fun getFreshMarathon(dates: List<LocalDate>?, isThisYear: Boolean): List<LocalDate> {
        val datesWithCurrent = dates?.plus(LocalDate.now())
        val days: MutableList<LocalDate> = mutableListOf()
        val maxDays: MutableList<LocalDate> = mutableListOf()
        var lastDate: LocalDate? = null
        val currentYear: LocalDate = LocalDate.ofYearDay(Year.now().value, 1)
        datesWithCurrent?.sorted()?.forEach sortedDates@{
            if (isThisYear) {
                if (!it.isBefore(currentYear)) if (lastDate?.isBefore(currentYear) == true) {
                    lastDate = currentYear.minusDays(1)
                } else {
                    lastDate = it
                    return@sortedDates
                }
            }
            lastDate?.let { lastDate ->
                val period = ChronoUnit.DAYS.between(lastDate, it) - 1
                days.clear()
                for (i in 0 until period) {
                    days.add(lastDate.plusDays(i + 1))
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
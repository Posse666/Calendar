package com.posse.kotlin1.calendar.viewModel

import androidx.lifecycle.Transformations
import java.time.LocalDate
import java.time.Year

const val THIS_YEAR = true
const val ALL_TIME = false

class StatisticViewModel : BaseStatsViewModel() {
    private val temperature = Transformations.map(repository.getTemperature()) { it }

    fun getDrinkMarathon(isThisYear: Boolean): Int {
        val days: ArrayList<LocalDate> = arrayListOf()
        val maxDays: ArrayList<LocalDate> = arrayListOf()
        val currentYear: LocalDate = LocalDate.ofYearDay(Year.now().value, 1)
        liveDataToObserve.value?.sorted()?.forEach {
            days.add(it)
            if (!days.contains(it.minusDays(1))) {
                if (isThisYear && (it.isBefore(currentYear) || days[0].isBefore(currentYear))) {
                    maxDays.clear()
                    val daysToDelete = arrayListOf<LocalDate>()
                    for (i in 0 until days.size) {
                        if (days[i].isBefore(currentYear)) {
                            daysToDelete.add(days[i])
                        }
                    }
                    days.removeAll(daysToDelete)
                }
                if (days.size > 2) days.removeAt(days.size - 1)
                if (maxDays.size <= days.size) {
                    maxDays.clear()
                    maxDays.addAll(days)
                }
                days.clear()
                days.add(it)
            }
        }
        return maxDays.size
    }

    fun getTemperature() = temperature
}
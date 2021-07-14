package com.posse.kotlin1.calendar.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.posse.kotlin1.calendar.model.repository.Repository
import com.posse.kotlin1.calendar.model.repository.RepositoryFirestoreImpl
import java.time.LocalDate
import java.time.Year
import java.time.temporal.ChronoUnit

const val THIS_YEAR = true
const val ALL_TIME = false

class CalendarViewModel : ViewModel() {

    private val repository: Repository = RepositoryFirestoreImpl("")
    private val liveDataToObserve: LiveData<Set<LocalDate>> =
        Transformations.map(repository.getLiveData()) { it }

    fun getLiveData() = liveDataToObserve

    fun dayClicked(date: LocalDate) = repository.changeState(date)

    fun getDrankDaysQuantity(): Int {
        var result = 0
        val currentYear: LocalDate = LocalDate.ofYearDay(Year.now().value, 1)
        liveDataToObserve.value?.forEach {
            if (!it.isBefore(currentYear)) result++
        }
        return result
    }

    fun getThisYearDaysQuantity(): Int {
        return (ChronoUnit.DAYS.between(
            LocalDate.ofYearDay(Year.now().value, 1),
            LocalDate.now()
        ) + 1).toInt()
    }

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
        return maxDays.size.coerceAtLeast(days.size)
    }
}
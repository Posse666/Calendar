package com.posse.kotlin1.calendar.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.posse.kotlin1.calendar.model.repository.Repository
import com.posse.kotlin1.calendar.model.repository.RepositoryFirestoreImpl
import java.time.LocalDate
import java.time.Year
import java.time.temporal.ChronoUnit

private const val THIS_YEAR = true
private const val ALL_TIME = false

class CalendarViewModel : ViewModel() {

    private val repository: Repository = RepositoryFirestoreImpl("")
    private val liveDataToObserve: LiveData<Set<LocalDate>> =
        Transformations.map(repository.getLiveData()) {
            liveStatisticToObserve.value = getSats(it.keys)
            it.keys
        }
    private val liveStatisticToObserve: MutableLiveData<Map<STATISTIC, Set<LocalDate>>> =
        MutableLiveData()

    private fun getSats(dates: Set<LocalDate>?): Map<STATISTIC, Set<LocalDate>> {
        val result = HashMap<STATISTIC, Set<LocalDate>>()
        result[STATISTIC.DAYS_THIS_YEAR] = getThisYearDaysQuantity()
        result[STATISTIC.DRINK_DAYS_THIS_YEAR] = getDrankDaysQuantity(dates)
        result[STATISTIC.DRINK_MAX_ROW_THIS_YEAR] = getDrinkMarathon(dates, THIS_YEAR)
        result[STATISTIC.DRINK_MAX_ROW_TOTAL] = getDrinkMarathon(dates, ALL_TIME)
        return result
    }

    fun getLiveData() = liveDataToObserve

    fun getLiveStats() = liveStatisticToObserve

    fun dayClicked(date: LocalDate) = repository.changeState(date)

    private fun getDrankDaysQuantity(dates: Set<LocalDate>?): Set<LocalDate> {
        val result = HashSet<LocalDate>()
        val currentYear: LocalDate = LocalDate.ofYearDay(Year.now().value, 1)
        dates?.forEach {
            if (!it.isBefore(currentYear)) result.add(it)
        }
        return result
    }

    private fun getThisYearDaysQuantity(): Set<LocalDate> {
        val result = HashSet<LocalDate>()
        val days = (ChronoUnit.DAYS.between(
            LocalDate.ofYearDay(Year.now().value, 1),
            LocalDate.now()
        ) + 1).toInt()
        for (i in 1..days) {
            result.add(LocalDate.now().plusDays(i.toLong()))
        }
        return result
    }

    private fun getDrinkMarathon(dates: Set<LocalDate>?, isThisYear: Boolean): Set<LocalDate> {
        val days: ArrayList<LocalDate> = arrayListOf()
        val maxDays: ArrayList<LocalDate> = arrayListOf()
        val currentYear: LocalDate = LocalDate.ofYearDay(Year.now().value, 1)
        dates?.sorted()?.forEach {
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
        return if (maxDays.size > days.size) {
            maxDays.toSet()
        } else {
            days.toSet()
        }
    }
}

enum class STATISTIC {
    DAYS_THIS_YEAR,
    DRINK_DAYS_THIS_YEAR,
    DRINK_MAX_ROW_THIS_YEAR,
    DRINK_MAX_ROW_TOTAL
}
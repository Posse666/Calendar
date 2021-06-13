package com.posse.kotlin1.calendar.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.posse.kotlin1.calendar.model.repository.Repository
import com.posse.kotlin1.calendar.model.repository.RepositoryImpl
import java.time.LocalDate
import java.time.Year
import java.time.temporal.ChronoUnit

abstract class BaseStatsViewModel : ViewModel() {

    protected val repository: Repository = RepositoryImpl()
    protected val liveDataToObserve: LiveData<Set<LocalDate>> = Transformations.map(repository.getLiveData()) { it }

    fun getLiveData() = liveDataToObserve

    fun refreshDrankState() = getDataFromLocalSource()

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

    private fun getDataFromLocalSource() {
        repository.removeLaterInitForTestingPurpose()
    }
}
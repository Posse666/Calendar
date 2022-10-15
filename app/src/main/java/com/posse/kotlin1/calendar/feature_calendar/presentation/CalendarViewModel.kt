package com.posse.kotlin1.calendar.feature_calendar.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.posse.kotlin1.calendar.common.domain.model.DrinkType
import com.posse.kotlin1.calendar.common.domain.model.Response
import com.posse.kotlin1.calendar.feature_calendar.domain.model.DayData
import com.posse.kotlin1.calendar.feature_calendar.domain.use_case.DatesUseCases
import com.posse.kotlin1.calendar.feature_calendar.domain.use_case.GetCalendarData
import com.posse.kotlin1.calendar.feature_calendar.domain.use_case.SendMessage
import com.posse.kotlin1.calendar.feature_calendar.presentation.model.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import kotlin.random.Random

open class CalendarViewModel(
    private val getCalendarData: GetCalendarData,
    private val datesUseCases: DatesUseCases,
    private val sendMessage: SendMessage,
    email: String,
) : ViewModel() {

    protected var mail: String = email

    private val _state = MutableStateFlow(CalendarState())
    val state get() = _state.asStateFlow()

    private val _statisticState = MutableStateFlow(StatisticWithDaysState())
    val statisticState = _statisticState.asStateFlow()

    private val _event = Channel<CalendarUIEvent>()
    val event get() = _event.receiveAsFlow()

    private val _scrollEvent = Channel<LocalDate>()
    val scrollEvent get() = _scrollEvent.receiveAsFlow()

    init {
        viewModelScope.launch {
            val calendarData = getCalendarData()
            _state.update { it.copy(calendarData = calendarData) }
            getUserData()
            _scrollEvent.send(LocalDate.now())
        }
    }

//    init {
//        setLoadingState(isLoading = true)
//        try {
////            mail = savedStateHandle["email"] ?: accountUseCases.getMyMail()
//
//            datesUseCases.getDates(mail).onEach { response ->
//                when (response) {
//                    is Response.Error -> handleError()
//                    is Response.Loading -> setLoadingState(isLoading = true)
//                    is Response.Success -> handleSuccessResult(response)
//                }
//            }
//        } catch (e: Exception) {
//            handleError()
//        }
//    }

    private fun getUserData() {
        _state.update { calendarState ->
            calendarState.copy(
                calendarData = calendarState.calendarData.map { monthData ->
                    monthData.copy(
                        weeks = monthData.weeks.map { week ->
                            week.map { dayData ->
                                val random = Random.nextFloat()
                                dayData?.copy(
                                    drinkType = if (random > 0.9) DrinkType.Full
                                    else if (random > 0.8) DrinkType.Half
                                    else null
                                )
                            }
                        }
                    )
                }
            )
        }
    }

    private fun setLoadingState(isLoading: Boolean) {
        _state.value = state.value.copy(isLoading = isLoading)
    }

    private fun handleSuccessResult(response: Response<List<DayData>>) {
        response.data?.let { daysData ->
            setScreenState(daysData.toSet())
        }
        setLoadingState(isLoading = false)
    }

    private fun handleError() {
        viewModelScope.launch {
            _event.send(CalendarUIEvent.ErrorLoading)
        }
        setLoadingState(isLoading = false)
    }

    fun onEvent(event: CalendarEvent) {
        when (event) {
            is CalendarEvent.DateClicked -> changeDay(event)
            is CalendarEvent.ToggleStatistic -> {
                _state.update { it.copy(isStatisticOpened = event.isExpanded) } //TODO save state
            }
            is CalendarEvent.StatisticClicked -> handleStatisticClick(event.statisticEntry)
            is CalendarEvent.StatsUsed -> _state.update { it.copy(isStatsEverShown = true) } //TODO save to settings
        }
    }

    private fun handleStatisticClick(statisticEntry: StatisticEntry) {
        viewModelScope.launch {
            val stats = when (statisticEntry) {
                StatisticEntry.DaysOverall -> emptyList()
                StatisticEntry.DrunkRowThisYear -> _statisticState.value.drunkRowThisYear
                StatisticEntry.DrunkRowOverall -> _statisticState.value.drunkRowOverall
                StatisticEntry.FreshRowThisYear -> _statisticState.value.freshRowThisYear
                StatisticEntry.FreshRowOverall -> _statisticState.value.freshRowOverall
            }
            _event.send(CalendarUIEvent.ScrollToSelectedStatistic(stats))
        }
    }

    private fun changeDay(dateClicked: CalendarEvent.DateClicked) {
        viewModelScope.launch {
//            val currentDates = state.value.calendarData
//                .mapNotNull {
//                    if (it is CalendarComponent.CalendarDay) it.dayData
//                    else null
//                }
//                .toMutableSet()
//            val isSuccess = when (dateClicked.day.drinkType) {
//                DrinkType.Full, DrinkType.Half -> {
//                    addDay(mail, dateClicked, currentDates).also { success ->
//                        if (success) sendMessage(mail, dateClicked.day)
//                    }
//                }
//                else -> removeDay(mail, dateClicked, currentDates)
//            }
//            if (isSuccess) setScreenState(currentDates)
//            else handleError()
        }
    }

    private suspend fun removeDay(
        mail: String,
        dateClicked: CalendarEvent.DateClicked,
        currentDates: MutableSet<DayData>
    ): Boolean {
        return if (datesUseCases.deleteDate(mail, dateClicked.day)) {
            currentDates.remove(dateClicked.day)
            true
        } else false
    }

    private suspend fun addDay(
        mail: String,
        dateClicked: CalendarEvent.DateClicked,
        currentDates: MutableSet<DayData>
    ): Boolean {
        return if (datesUseCases.setDate(mail, dateClicked.day)) {
            currentDates.add(dateClicked.day)
            true
        } else false
    }

    private fun setScreenState(currentDates: Set<DayData>) {
        val statistic = datesUseCases.calculateStatistic(currentDates)
        _statisticState.value = statistic
//        _state.value = state.value.copy(
//            dates = currentDates,
//            statistic = StatisticState(
//                totalDaysThisYear = statistic.daysOverall.size,
//                drinkRowThisYear = statistic.drunkRowThisYear.size,
//                drinkRowAllTime = statistic.drunkRowOverall.size,
//                freshRowThisYear = statistic.freshRowThisYear.size,
//                freshRowAllTime = statistic.freshRowOverall.size
//            )
//        )
    }
}
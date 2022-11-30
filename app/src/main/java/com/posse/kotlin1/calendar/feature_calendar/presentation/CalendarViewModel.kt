package com.posse.kotlin1.calendar.feature_calendar.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.posse.kotlin1.calendar.common.domain.model.DrinkType
import com.posse.kotlin1.calendar.feature_calendar.domain.model.DayData
import com.posse.kotlin1.calendar.feature_calendar.domain.use_cases.CalculateMonthIndex
import com.posse.kotlin1.calendar.feature_calendar.domain.use_cases.DatesUseCases
import com.posse.kotlin1.calendar.feature_calendar.domain.use_cases.GetCalendarData
import com.posse.kotlin1.calendar.feature_calendar.domain.use_cases.SendMessage
import com.posse.kotlin1.calendar.feature_calendar.presentation.model.CalendarEvent
import com.posse.kotlin1.calendar.feature_calendar.presentation.model.CalendarState
import com.posse.kotlin1.calendar.feature_calendar.presentation.model.CalendarUIEvent
import com.posse.kotlin1.calendar.feature_calendar.presentation.model.StatisticEntry
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import kotlin.random.Random

open class CalendarViewModel(
    private val getCalendarData: GetCalendarData,
    private val datesUseCases: DatesUseCases,
    private val calculateMonthIndex: CalculateMonthIndex,
    private val sendMessage: SendMessage,
    email: String,
) : ViewModel() {

    protected var mail: String = email

    private val _state = MutableStateFlow(CalendarState())
    val state = _state.asStateFlow()

    private val _event = Channel<CalendarUIEvent>()
    val event = _event.receiveAsFlow()

    private val _scrollEvent = Channel<Int>()
    val scrollEvent = _scrollEvent.receiveAsFlow()

    private val _animateScrollEvent = MutableSharedFlow<Int>()
    val animateScrollEvent = _animateScrollEvent.asSharedFlow()

    var statisticAnimationJob: Job? = null

    init {
        viewModelScope.launch {
            val calendarData = getCalendarData()
            _state.update { it.copy(calendarData = calendarData) }
            getUserData()
            scrollToDate(LocalDate.now())
            launchStatisticAnimation()
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
        setRandomData()
        updateStatistic(state.value.calendarData.flatMap { it.weeks.flatten().filterNotNull() }
            .toSet())
    }

    private fun setRandomData() { //TODO Remove
        _state.update { calendarState ->
            calendarState.copy(calendarData = calendarState.calendarData.map { monthData ->
                monthData.copy(weeks = monthData.weeks.map { week ->
                    week.map { dayData ->
                        val random = Random.nextFloat()
                        dayData?.copy(
                            drinkType = if (random > 0.9) DrinkType.Full
                            else if (random > 0.8) DrinkType.Half
                            else null
                        )
                    }
                })
            })
        }
    }

    private fun setLoadingState(isLoading: Boolean) {
        _state.value = state.value.copy(isLoading = isLoading)
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
            is CalendarEvent.ToggleStatistic -> onStatisticToggle(event.isExpanded)
            is CalendarEvent.StatisticClicked -> handleStatisticClick(event.statisticEntry)
            is CalendarEvent.BackToCurrentDate -> animateScrollToDate(LocalDate.now())
        }
    }

    private fun animateScrollToDate(date: LocalDate) {
        viewModelScope.launch {
            val index = calculateMonthIndex(
                calendarData = _state.value.calendarData,
                date = date
            ) ?: return@launch
            _animateScrollEvent.emit(index)
        }
    }

    private fun scrollToDate(date: LocalDate) {
        viewModelScope.launch {
            val index = calculateMonthIndex(
                calendarData = _state.value.calendarData,
                date = date
            ) ?: return@launch
            _scrollEvent.send(index)
        }
    }

    private fun onStatisticToggle(expanded: Boolean) {
        if (!_state.value.isStatsEverShown && expanded) {
            _state.update { it.copy(isStatsEverShown = true) }
            // TODO save stats ever shown
        }
    }

    private fun handleStatisticClick(statisticEntry: StatisticEntry) {
        viewModelScope.launch {
            val stats = when (statisticEntry) {
                StatisticEntry.DaysOverall -> emptyList()
                StatisticEntry.DrunkRowThisYear -> state.value.statistic.drunkRowThisYear
                StatisticEntry.DrunkRowOverall -> state.value.statistic.drunkRowOverall
                StatisticEntry.FreshRowThisYear -> state.value.statistic.freshRowThisYear
                StatisticEntry.FreshRowOverall -> state.value.statistic.freshRowOverall
            }.ifEmpty { return@launch }
            animateScrollToDate(stats.first())
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

    private fun launchStatisticAnimation() {
        statisticAnimationJob?.cancel()
        statisticAnimationJob = viewModelScope.launch {
            while (!state.value.isStatsEverShown) {
                delay(30_000)
                if (state.value.isStatsEverShown) return@launch
                _state.update { it.copy(isStatsExpanded = true) }
                delay(1_000)
                if (state.value.isStatsEverShown) return@launch
                _state.update { it.copy(isStatsExpanded = false) }
            }
        }
    }

    private suspend fun removeDay(
        mail: String, dateClicked: CalendarEvent.DateClicked
    ): Boolean {
        return datesUseCases.deleteDate(mail, dateClicked.day)
    }

    private suspend fun addDay(
        mail: String, dateClicked: CalendarEvent.DateClicked
    ): Boolean {
        return datesUseCases.setDate(mail, dateClicked.day)
    }

    private fun updateStatistic(currentDates: Set<DayData>) {
        viewModelScope.launch() { }
        val statistic = datesUseCases.calculateStatistic(currentDates)
        _state.update { it.copy(statistic = statistic) }
    }
}
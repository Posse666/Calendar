package com.posse.kotlin1.calendar.feature_calendar.presentation

import com.posse.kotlin1.calendar.common.di.Inject
import com.posse.kotlin1.calendar.common.domain.model.DrinkType
import com.posse.kotlin1.calendar.common.utils.DateTimeUtils
import com.posse.kotlin1.calendar.common.viewModel.BaseSharedViewModel
import com.posse.kotlin1.calendar.feature_calendar.domain.model.DayData
import com.posse.kotlin1.calendar.feature_calendar.domain.use_cases.CalendarUseCases
import com.posse.kotlin1.calendar.feature_calendar.domain.use_cases.SendMessage
import com.posse.kotlin1.calendar.feature_calendar.presentation.model.CalendarAction
import com.posse.kotlin1.calendar.feature_calendar.presentation.model.CalendarEvent
import com.posse.kotlin1.calendar.feature_calendar.presentation.model.CalendarState
import com.posse.kotlin1.calendar.feature_calendar.presentation.model.StatisticEntry
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import moe.tlaster.precompose.viewmodel.viewModelScope
import kotlin.random.Random

open class CalendarViewModel(
    email: String?
) : BaseSharedViewModel<CalendarState, CalendarAction, CalendarEvent>(
    initialState = CalendarState()
) {
    private val calendarUseCases: CalendarUseCases = Inject.instance()
    private val sendMessage: SendMessage = Inject.instance()

    protected var mail: String? = email
    private var statisticAnimationJob: Job? = null

    init {
        withViewModelScope {
            val calendarData = calendarUseCases.getCalendarData()
            _viewState.update { it.copy(calendarData = calendarData) }
            getUserData()
            scrollToDate(DateTimeUtils.today)
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
        updateStatistic(
            viewState.value.calendarData
                .flatMap { it.weeks.flatten().filterNotNull() }
                .toSet()
        )
    }

    private fun setRandomData() { //TODO Remove
        _viewState.update {
            it.copy(
                calendarData = viewState.value.calendarData.map { monthData ->
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
        _viewState.update { it.copy(isLoading = isLoading) }
    }

    private fun handleError() {
        _viewAction.tryEmit(CalendarAction.ErrorLoading)
        setLoadingState(isLoading = false)
    }

    override fun obtainEvent(viewEvent: CalendarEvent) {
        when (viewEvent) {
            is CalendarEvent.DateClicked -> changeDay(viewEvent)
            is CalendarEvent.ToggleStatistic -> onStatisticToggle(viewEvent.isExpanded)
            is CalendarEvent.StatisticClicked -> handleStatisticClick(viewEvent.statisticEntry)
            is CalendarEvent.BackToCurrentDate -> scrollToDate(DateTimeUtils.today, animate = true)
        }
    }

    private fun scrollToDate(date: LocalDate, animate: Boolean = false) {
        withViewModelScope {
            val index = calendarUseCases.calculateMonthIndex(
                calendarData = viewState.value.calendarData,
                date = date
            ) ?: return@withViewModelScope
            _viewAction.emit(CalendarAction.ScrollToIndex(index, animate))
        }
    }

    private fun onStatisticToggle(expanded: Boolean) {
        if (!viewState.value.isStatsEverShown && expanded) {
            _viewState.update { it.copy(isStatsEverShown = true) }
            // TODO save stats ever shown
        }
    }

    private fun handleStatisticClick(statisticEntry: StatisticEntry) {
        withViewModelScope {
            val stats = when (statisticEntry) {
                StatisticEntry.DaysOverall -> emptyList()
                StatisticEntry.DrunkRowThisYear -> viewState.value.statistic.drunkRowThisYear
                StatisticEntry.DrunkRowOverall -> viewState.value.statistic.drunkRowOverall
                StatisticEntry.FreshRowThisYear -> viewState.value.statistic.freshRowThisYear
                StatisticEntry.FreshRowOverall -> viewState.value.statistic.freshRowOverall
            }.ifEmpty { return@withViewModelScope }
            scrollToDate(stats.first(), animate = true)
        }
    }

    private fun changeDay(dateClicked: CalendarEvent.DateClicked) {
        withViewModelScope {
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
            while (!viewState.value.isStatsEverShown) {
                delay(30_000)
                if (viewState.value.isStatsEverShown) return@launch
                _viewState.update { it.copy(isStatsExpanded = true) }
                delay(1_000)
                if (viewState.value.isStatsEverShown) return@launch
                _viewState.update { it.copy(isStatsExpanded = false) }
            }
        }
    }

    private suspend fun removeDay(
        mail: String, dateClicked: CalendarEvent.DateClicked
    ): Boolean = calendarUseCases.deleteDate(mail, dateClicked.day)

    private suspend fun addDay(
        mail: String, dateClicked: CalendarEvent.DateClicked
    ): Boolean = calendarUseCases.setDate(mail, dateClicked.day)

    private fun updateStatistic(currentDates: Set<DayData>) {
        val statistic = calendarUseCases.calculateStatistic(currentDates)
        _viewState.update { it.copy(statistic = statistic) }
    }
}
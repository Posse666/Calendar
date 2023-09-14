package com.posse.kotlin1.calendar.feature_calendar.presentation

import com.posse.kotlin1.calendar.common.di.Inject
import com.posse.kotlin1.calendar.common.domain.model.DrinkType
import com.posse.kotlin1.calendar.common.utils.DateTimeUtils
import com.posse.kotlin1.calendar.common.viewModel.BaseSharedViewModel
import com.posse.kotlin1.calendar.feature_calendar.domain.model.DayData
import com.posse.kotlin1.calendar.feature_calendar.domain.use_cases.CalculateMonthIndex
import com.posse.kotlin1.calendar.feature_calendar.domain.use_cases.DatesUseCases
import com.posse.kotlin1.calendar.feature_calendar.domain.use_cases.GetCalendarData
import com.posse.kotlin1.calendar.feature_calendar.domain.use_cases.SendMessage
import com.posse.kotlin1.calendar.feature_calendar.presentation.model.CalendarAction
import com.posse.kotlin1.calendar.feature_calendar.presentation.model.CalendarEvent
import com.posse.kotlin1.calendar.feature_calendar.presentation.model.CalendarState
import com.posse.kotlin1.calendar.feature_calendar.presentation.model.StatisticEntry
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import moe.tlaster.precompose.viewmodel.viewModelScope
import kotlin.random.Random

open class CalendarViewModel(
    email: String
) : BaseSharedViewModel<CalendarState, CalendarAction, CalendarEvent>(
    initialState = CalendarState()
) {
    private val getCalendarData: GetCalendarData = Inject.instance()
    private val datesUseCases: DatesUseCases = Inject.instance()
    private val calculateMonthIndex: CalculateMonthIndex = Inject.instance()
    private val sendMessage: SendMessage = Inject.instance()

    protected var mail: String = email
    private var statisticAnimationJob: Job? = null

    init {
        val calendarData = getCalendarData()
        viewState = viewState.copy(calendarData = calendarData)
        getUserData()
        scrollToDate(DateTimeUtils.today)
        launchStatisticAnimation()
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
            viewState.calendarData
                .flatMap { it.weeks.flatten().filterNotNull() }
                .toSet()
        )
    }

    private fun setRandomData() { //TODO Remove
        viewState = viewState.copy(
            calendarData = viewState.calendarData.map { monthData ->
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

    private fun setLoadingState(isLoading: Boolean) {
        viewState = viewState.copy(isLoading = isLoading)
    }

    private fun handleError() {
        viewAction = CalendarAction.ErrorLoading
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
            val index = calculateMonthIndex(
                calendarData = viewState.calendarData,
                date = date
            ) ?: return@withViewModelScope
            CalendarAction.ScrollToIndex(index, animate)
        }
    }

    private fun onStatisticToggle(expanded: Boolean) {
        if (!viewState.isStatsEverShown && expanded) {
            viewState = viewState.copy(isStatsEverShown = true)
            // TODO save stats ever shown
        }
    }

    private fun handleStatisticClick(statisticEntry: StatisticEntry) {
        withViewModelScope {
            val stats = when (statisticEntry) {
                StatisticEntry.DaysOverall -> emptyList()
                StatisticEntry.DrunkRowThisYear -> viewState.statistic.drunkRowThisYear
                StatisticEntry.DrunkRowOverall -> viewState.statistic.drunkRowOverall
                StatisticEntry.FreshRowThisYear -> viewState.statistic.freshRowThisYear
                StatisticEntry.FreshRowOverall -> viewState.statistic.freshRowOverall
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
            while (!viewState.isStatsEverShown) {
                delay(30_000)
                if (viewState.isStatsEverShown) return@launch
                viewState = viewState.copy(isStatsExpanded = true)
                delay(1_000)
                if (viewState.isStatsEverShown) return@launch
                viewState = viewState.copy(isStatsExpanded = false)
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
        val statistic = datesUseCases.calculateStatistic(currentDates)
        viewState = viewState.copy(statistic = statistic)
    }
}
package com.posse.kotlin1.calendar.feature_calendar.presentation

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.posse.kotlin1.calendar.common.domain.model.Response
import com.posse.kotlin1.calendar.common.domain.use_case.AccountUseCases
import com.posse.kotlin1.calendar.feature_calendar.domain.model.DayData
import com.posse.kotlin1.calendar.feature_calendar.domain.use_case.DatesUseCases
import com.posse.kotlin1.calendar.feature_calendar.domain.use_case.SendMessage
import com.posse.kotlin1.calendar.feature_calendar.presentation.model.*
import com.posse.kotlin1.calendar.view.calendar.DrinkType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CalendarViewModel @Inject constructor(
    private val datesUseCases: DatesUseCases,
    private val accountUseCases: AccountUseCases,
    private val sendMessage: SendMessage,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private var mail: String? = savedStateHandle["email"]

    private val _state = MutableStateFlow(CalendarState())
    val state get() = _state.asStateFlow()

    private val _event = Channel<CalendarUIEvent>()
    val event get() = _event.receiveAsFlow()

    private val statisticState = mutableStateOf(StatisticWithDaysState())

    init {
        setLoadingState(isLoading = true)
        try {
            viewModelScope.launch {
                if (mail == null) {
                    mail = accountUseCases.getMyMail()
                }
                mail?.let { mail ->
                    datesUseCases.getDates(mail).onEach { response ->
                        when (response) {
                            is Response.Error -> handleError()
                            is Response.Loading -> setLoadingState(isLoading = true)
                            is Response.Success -> handleSuccessResult(response)
                        }
                    }
                } ?: handleError()
            }
        } catch (e: Exception) {
            handleError()
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
                _state.value = state.value.copy(isStatisticOpened = event.isExpanded)
            }
            is CalendarEvent.StatisticClicked -> handleStatisticClick(event.statisticEntry)
        }
    }

    private fun handleStatisticClick(statisticEntry: StatisticEntry) {
        viewModelScope.launch {
            val stats = when (statisticEntry) {
                StatisticEntry.DaysOverall -> emptyList()
                StatisticEntry.DrunkRowThisYear -> statisticState.value.drunkRowThisYear
                StatisticEntry.DrunkRowOverall -> statisticState.value.drunkRowOverall
                StatisticEntry.FreshRowThisYear -> statisticState.value.freshRowThisYear
                StatisticEntry.FreshRowOverall -> statisticState.value.freshRowOverall
            }
            _event.send(CalendarUIEvent.ScrollToSelectedStatistic(stats))
        }
    }

    private fun changeDay(dateClicked: CalendarEvent.DateClicked) {
        mail?.let { mail ->
            viewModelScope.launch {
                val currentDates = state.value.dates.toMutableSet()
                val isSuccess = when (dateClicked.day.drinkType) {
                    DrinkType.Full.value, DrinkType.Half.value -> {
                        addDay(mail, dateClicked, currentDates).also { success ->
                            if (success) sendMessage(mail, dateClicked.day)
                        }
                    }
                    else -> removeDay(mail, dateClicked, currentDates)
                }
                if (isSuccess) setScreenState(currentDates)
                else handleError()
            }
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
        statisticState.value = statistic
        _state.value = state.value.copy(
            dates = currentDates,
            statistic = StatisticState(
                totalDaysThisYear = statistic.daysOverall.size,
                drinkRowThisYear = statistic.drunkRowThisYear.size,
                drinkRowAllTime = statistic.drunkRowOverall.size,
                freshRowThisYear = statistic.freshRowThisYear.size,
                freshRowAllTime = statistic.freshRowOverall.size
            )
        )
    }
}
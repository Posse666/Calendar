package com.posse.kotlin1.calendar.feature_calendar.compose.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ElevatedCard
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.posse.kotlin1.calendar.common.utils.DateTimeUtils
import com.posse.kotlin1.calendar.feature_calendar.domain.model.MonthData
import com.posse.kotlin1.calendar.feature_calendar.presentation.model.CalendarAction
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.datetime.LocalDate

@Composable
fun CalendarCustomView(
    onDayClick: (LocalDate) -> Unit,
    calendarData: () -> List<MonthData>,
    isCurrentMonthVisible: (Boolean) -> Unit,
    onScrollCompleted: () -> Unit,
    viewAction: SharedFlow<CalendarAction>,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()

    val currentMonthIndex by remember(calendarData()) {
        val data = calendarData()
        val today = DateTimeUtils.today
        val calendarMonth = data
            .find { monthData ->
                monthData.weeks.flatMap { week ->
                    week.map { it?.date }
                }.contains(today)
            }
            ?: return@remember mutableStateOf(if (data.size > 1) data.size - 1 else 0)

        mutableStateOf(data.indexOf(calendarMonth))
    }

    LaunchedEffect(listState) {
        snapshotFlow { listState.firstVisibleItemIndex }
            .map { it >= currentMonthIndex }
            .distinctUntilChanged()
            .collect { isCurrentMonthVisible(it) }
    }

    LaunchedEffect(key1 = true) {
        viewAction.collect {
            when (it) {
                is CalendarAction.ScrollToIndex -> {
                    if (it.animate) {
                        listState.animateScrollToItem(it.index)

                    } else {
                        listState.scrollToItem(it.index)
                        onScrollCompleted()
                    }
                }

                else -> Unit
            }
        }
    }

    LazyColumn(
        modifier = modifier,
        state = listState,
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
    ) {
        items(
            items = calendarData(),
            key = { it.hashCode() }
        ) { calendarMonth ->
            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                CalendarMonthItem(
                    monthData = calendarMonth,
                    onDayClick = onDayClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                )
            }
        }
    }
}
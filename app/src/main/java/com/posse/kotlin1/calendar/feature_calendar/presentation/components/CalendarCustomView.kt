package com.posse.kotlin1.calendar.feature_calendar.presentation.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ElevatedCard
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.posse.kotlin1.calendar.feature_calendar.domain.model.MonthData
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Composable
fun CalendarCustomView(
    onDayClick: (LocalDate) -> Unit,
    calendarData: List<MonthData>,
    modifier: Modifier = Modifier,
    scrollToDate: Flow<LocalDate>,
    onScrollCompleted: () -> Unit
) {
    val state = rememberLazyListState()

    LaunchedEffect(key1 = true) {
        scrollToDate.collect { scrollDate ->
            val calendarMonth = calendarData
                .find { monthData ->
                    monthData.weeks.flatMap { week ->
                        week.map { it?.date }
                    }.contains(scrollDate)
                }
                ?: return@collect

            val index = calendarData.indexOf(calendarMonth)

            state.scrollToItem(index)
            onScrollCompleted()
        }
    }

    LazyColumn(
        modifier = modifier,
        state = state
    ) {
        calendarData.forEach { calendarMonth ->
            item(calendarMonth.yearMonth) {
                ElevatedCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
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
}
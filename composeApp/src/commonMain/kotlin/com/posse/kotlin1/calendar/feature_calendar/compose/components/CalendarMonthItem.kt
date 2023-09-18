package com.posse.kotlin1.calendar.feature_calendar.compose.components

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.posse.kotlin1.calendar.feature_calendar.domain.model.MonthData
import kotlinx.datetime.LocalDate

@Composable
fun CalendarMonthItem(
    monthData: MonthData,
    onDayClick: (LocalDate) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        CalendarMonthHeader(
            year = monthData.year,
            month = monthData.month,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        CalendarDaysComponent(
            weeks = monthData.weeks,
            onDayClick = onDayClick,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
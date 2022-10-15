package com.posse.kotlin1.calendar.feature_calendar.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Text
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import java.time.DayOfWeek
import java.time.YearMonth
import java.time.format.TextStyle
import java.time.temporal.WeekFields
import java.util.*

@Composable
fun CalendarMonthHeader(
    yearMonth: YearMonth,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        val text = remember {
            buildString {
                val monthName = yearMonth.month
                    .getDisplayName(TextStyle.FULL_STANDALONE, Locale.getDefault())
                    .lowercase()
                    .replaceFirstChar {
                        if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
                    }

                append(monthName)
                append(" ")
                append(yearMonth.year)
            }
        }

        Text(
            text = text,
            style = MaterialTheme.typography.displaySmall
        )

        Row(modifier = Modifier.fillMaxWidth()) {
            weekdays.forEach { day ->
                val dayText = remember {
                    day.getDisplayName(TextStyle.SHORT, Locale.getDefault())
                        .lowercase()
                        .replaceFirstChar {
                            if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
                        }
                }

                Text(
                    text = dayText,
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (day == DayOfWeek.SUNDAY || day == DayOfWeek.SATURDAY) Color.Red
                    else LocalContentColor.current,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

private val weekdays: List<DayOfWeek> by lazy {
    val firstDay = WeekFields.of(Locale.getDefault()).firstDayOfWeek
    (0 until 7L).map { firstDay.plus(it) }
}
package com.posse.kotlin1.calendar.feature_calendar.compose.components

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
import com.posse.kotlin1.calendar.feature_calendar.utils.Days
import com.posse.kotlin1.calendar.feature_calendar.utils.Months
import dev.icerock.moko.resources.compose.stringResource
import kotlinx.datetime.Month

@Composable
fun CalendarMonthHeader(
    year: Int,
    month: Month,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        val monthName = stringResource(Months.entries[month.ordinal].nameResource)
        val text = remember { "$monthName $year" }

        Text(
            text = text,
            style = MaterialTheme.typography.displaySmall
        )

        Row(modifier = Modifier.fillMaxWidth()) {
            Days.entries.forEach { day ->
                Text(
                    text = stringResource(day.nameResource),
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (day.ordinal == 5 || day.ordinal == 6) Color.Red
                    else LocalContentColor.current,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}
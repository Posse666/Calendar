package com.posse.kotlin1.calendar.feature_calendar.compose.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.dp
import com.posse.kotlin1.calendar.feature_calendar.presentation.model.CalendarEvent
import com.posse.kotlin1.calendar.feature_calendar.presentation.model.StatisticEntry
import com.posse.kotlin1.calendar.feature_calendar.presentation.model.StatisticWithDaysState
import dev.icerock.moko.resources.compose.stringResource
import resources.MR

@Composable
fun BottomSheet(
    statistic: StatisticWithDaysState,
    onSizeMeasured: (Int) -> Unit,
    onStatClicked: (CalendarEvent.StatisticClicked) -> Unit,
): @Composable ColumnScope.() -> Unit = {
    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .onGloballyPositioned { onSizeMeasured(it.size.height) }
    ) {
        StatisticRow(
            name = stringResource(MR.strings.in_this_year_you_drank),
            stat = statistic.daysOverall.size.toString(),
            onStatClick = { onStatClicked(CalendarEvent.StatisticClicked(StatisticEntry.DaysOverall)) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        )

        StatisticRow(
            name = stringResource(MR.strings.longest_drink_marathon_in_this_year),
            stat = statistic.drunkRowThisYear.size.toString(),
            onStatClick = { onStatClicked(CalendarEvent.StatisticClicked(StatisticEntry.DrunkRowThisYear)) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        )

        StatisticRow(
            name = stringResource(MR.strings.longest_drink_marathon_all_time),
            stat = statistic.drunkRowOverall.size.toString(),
            onStatClick = { onStatClicked(CalendarEvent.StatisticClicked(StatisticEntry.DrunkRowOverall)) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        )

        StatisticRow(
            name = stringResource(MR.strings.longest_fresh_marathon_in_this_year),
            stat = statistic.freshRowThisYear.size.toString(),
            onStatClick = { onStatClicked(CalendarEvent.StatisticClicked(StatisticEntry.DrunkRowThisYear)) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        )

        StatisticRow(
            name = stringResource(MR.strings.longest_fresh_marathon_all_time),
            stat = statistic.freshRowOverall.size.toString(),
            onStatClick = { onStatClicked(CalendarEvent.StatisticClicked(StatisticEntry.FreshRowOverall)) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        )
    }
}
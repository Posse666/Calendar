package com.posse.kotlin1.calendar.feature_calendar.presentation.components

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.posse.kotlin1.calendar.R
import com.posse.kotlin1.calendar.feature_calendar.presentation.model.StatisticState

@SuppressLint("ComposableNaming")
@Composable
fun BottomSheet(
    statistic: StatisticState
): @Composable ColumnScope.() -> Unit = {
    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
        StatisticRow(
            name = stringResource(id = R.string.in_this_year_you_drank),
            stat = statistic.totalDaysThisYear.toString(),
            onStatClick = { /*TODO*/ },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        )

        StatisticRow(
            name = stringResource(id = R.string.longest_drink_marathon_in_this_year),
            stat = statistic.drinkRowThisYear.toString(),
            onStatClick = { /*TODO*/ },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        )

        StatisticRow(
            name = stringResource(id = R.string.longest_drink_marathon_all_time),
            stat = statistic.drinkRowAllTime.toString(),
            onStatClick = { /*TODO*/ },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        )

        StatisticRow(
            name = stringResource(id = R.string.longest_fresh_marathon_in_this_year),
            stat = statistic.freshRowThisYear.toString(),
            onStatClick = { /*TODO*/ },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        )

        StatisticRow(
            name = stringResource(id = R.string.longest_fresh_marathon_all_time),
            stat = statistic.freshRowAllTime.toString(),
            onStatClick = { /*TODO*/ },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        )
    }
}
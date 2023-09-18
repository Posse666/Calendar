package com.posse.kotlin1.calendar.feature_calendar.compose

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateIntOffsetAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.BottomSheetScaffold
import androidx.compose.material.BottomSheetValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.rememberBottomSheetScaffoldState
import androidx.compose.material.rememberBottomSheetState
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.posse.kotlin1.calendar.feature_calendar.compose.components.BottomSheet
import com.posse.kotlin1.calendar.feature_calendar.compose.components.CalendarCustomView
import com.posse.kotlin1.calendar.feature_calendar.presentation.CalendarViewModel
import com.posse.kotlin1.calendar.feature_calendar.presentation.model.CalendarAction
import com.posse.kotlin1.calendar.feature_calendar.presentation.model.CalendarEvent
import dev.icerock.moko.resources.compose.stringResource
import moe.tlaster.precompose.flow.collectAsStateWithLifecycle
import resources.MR

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun CalendarView(
    viewModel: CalendarViewModel,
    topBar: (@Composable () -> Unit)? = null
) {
    val state by viewModel.viewState.collectAsStateWithLifecycle()
    val viewAction = remember { viewModel.viewAction }
    val bottomSheetState = rememberBottomSheetState(initialValue = BottomSheetValue.Collapsed)

    val sheetHeight by animateDpAsState(
        targetValue = if (state.isStatsExpanded) 200.dp else 100.dp,
        animationSpec = tween(700)
    )

    var isCurrentMonthVisible by remember { mutableStateOf(true) }

    var fabAdditionalOffset by remember { mutableStateOf(0) }

    val fabOffset by animateIntOffsetAsState(
        targetValue = IntOffset(
            x = 0,
            y = if (isCurrentMonthVisible) -100 else 200 + fabAdditionalOffset
        ),
        animationSpec = tween(700)
    )

    LaunchedEffect(key1 = true) {
        viewAction.collect {
            when (it) {
                is CalendarAction.ErrorLoading -> TODO()
                is CalendarAction.ScrollToIndex -> bottomSheetState.collapse()
            }
        }
    }

    LaunchedEffect(bottomSheetState.isExpanded) {
        viewModel.obtainEvent(CalendarEvent.ToggleStatistic(bottomSheetState.isExpanded))
    }

    BottomSheetScaffold(
        topBar = topBar,
        scaffoldState = rememberBottomSheetScaffoldState(bottomSheetState = bottomSheetState),
        sheetPeekHeight = sheetHeight,
        sheetShape = MaterialTheme.shapes.medium.copy(
            bottomEnd = CornerSize(0),
            bottomStart = CornerSize(0)
        ),
        sheetContent = BottomSheet(
            statistic = state.statistic,
            onSizeMeasured = { if (fabAdditionalOffset < it) fabAdditionalOffset = it },
            onStatClicked = viewModel::obtainEvent
        ),
        floatingActionButton = {
            FloatingActionButton(
                shape = CircleShape,
                onClick = { viewModel.obtainEvent(CalendarEvent.BackToCurrentDate) },
                modifier = Modifier.offset { fabOffset }
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowDownward,
                    contentDescription = stringResource(MR.strings.backToDateButton)
                )
            }
        },
    ) { paddingValues ->
        CalendarCustomView(
            onDayClick = remember {
                {
                    if (state.isMyCalendar) {
                        //TODO
                    } else Unit
                }
            },
            calendarData = remember { { state.calendarData } },
            onScrollCompleted = remember { {/*TODO*/ } },
            isCurrentMonthVisible = remember { { isCurrentMonthVisible = !it } },
            viewAction = remember { viewAction },
            modifier = Modifier.padding(paddingValues)
        )
    }
}
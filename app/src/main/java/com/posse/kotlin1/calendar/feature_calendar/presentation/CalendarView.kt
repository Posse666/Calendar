package com.posse.kotlin1.calendar.feature_calendar.presentation

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateIntOffsetAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.posse.kotlin1.calendar.R
import com.posse.kotlin1.calendar.feature_calendar.presentation.components.BottomSheet
import com.posse.kotlin1.calendar.feature_calendar.presentation.components.CalendarCustomView
import com.posse.kotlin1.calendar.feature_calendar.presentation.model.CalendarEvent
import com.posse.kotlin1.calendar.feature_calendar.presentation.model.CalendarUIEvent

@OptIn(
    ExperimentalMaterialApi::class,
    ExperimentalLifecycleComposeApi::class,
)
@Composable
fun CalendarView(
    viewModel: CalendarViewModel,
    topBar: (@Composable () -> Unit)? = null
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    val bottomSheetState = rememberBottomSheetState(initialValue = BottomSheetValue.Collapsed)

    val coroutineScope = rememberCoroutineScope()

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
        viewModel.event.collect { event ->
            when (event) {
                is CalendarUIEvent.ErrorLoading -> {} // TODO
            }
        }
    }

    LaunchedEffect(bottomSheetState.isExpanded) {
        viewModel.onEvent(CalendarEvent.ToggleStatistic(bottomSheetState.isExpanded))
    }

    LaunchedEffect(key1 = true) {
        viewModel.animateScrollEvent.collect { bottomSheetState.collapse() }
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
            onStatClicked = viewModel::onEvent
        ),
        floatingActionButton = {
            FloatingActionButton(
                shape = CircleShape,
                onClick = { viewModel.onEvent(CalendarEvent.BackToCurrentDate) },
                modifier = Modifier.offset { fabOffset }
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowDownward,
                    contentDescription = stringResource(id = R.string.back_to_date_button)
                )
            }
        },
    ) { paddingValues ->

        CalendarCustomView(
            onDayClick = remember {{
                if (state.isMyCalendar) {
                    //TODO
                } else Unit
            }},
            calendarData =remember { {state.calendarData}},
            scrollToDateIndex = remember { viewModel.scrollEvent },
            animateScrollToDateIndex =  remember { viewModel.animateScrollEvent},
            onScrollCompleted = remember{ {/*TODO*/ }},
            isCurrentMonthVisible = remember {{ isCurrentMonthVisible = !it }},
            modifier = Modifier.padding(paddingValues)
        )
    }
}
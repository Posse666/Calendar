package com.posse.kotlin1.calendar.feature_calendar.presentation

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material3.FloatingActionButton
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.posse.kotlin1.calendar.feature_calendar.presentation.components.BottomSheet
import com.posse.kotlin1.calendar.feature_calendar.presentation.components.CalendarCustomView
import com.posse.kotlin1.calendar.feature_calendar.presentation.model.CalendarEvent
import kotlinx.coroutines.delay

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

    val bottomSheetScaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = BottomSheetState(BottomSheetValue.Collapsed)
    )
    val coroutineScope = rememberCoroutineScope()
    val bottomSheetMinPickHeight = remember { 100.dp }

    var autoExpandSheet by remember { mutableStateOf(false) }

    val sheetHeight by animateDpAsState(
        targetValue = if (autoExpandSheet) 200.dp
        else bottomSheetMinPickHeight,
        animationSpec = tween(700)
    )

    LaunchedEffect(key1 = true) {
        while (!state.isStatsEverShown) {
            delay(30_000)
            if (state.isStatsEverShown) return@LaunchedEffect
            autoExpandSheet = true
            delay(1_000)
            if (state.isStatsEverShown) return@LaunchedEffect
            autoExpandSheet = false
        }
    }

    LaunchedEffect(key1 = bottomSheetScaffoldState.bottomSheetState) {
        if (!state.isStatsEverShown && bottomSheetScaffoldState.bottomSheetState.isExpanded) {
            viewModel.onEvent(CalendarEvent.StatsUsed)
        }
    }

    BottomSheetScaffold(
        topBar = topBar,
        scaffoldState = bottomSheetScaffoldState,
        sheetPeekHeight = sheetHeight,
        sheetContent = BottomSheet(statistic = state.statistic),
        floatingActionButton = {
            FloatingActionButton(
                onClick = { /*TODO*/ }
            ) {
            }
        },
    ) { paddingValues ->

        CalendarCustomView(
            onDayClick = {},
            calendarData = state.calendarData,
            scrollToDate = viewModel.scrollEvent,
            onScrollCompleted = {},
            modifier = Modifier.padding(paddingValues)
        )
    }
}
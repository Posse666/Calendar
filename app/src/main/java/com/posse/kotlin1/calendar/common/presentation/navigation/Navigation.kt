package com.posse.kotlin1.calendar.common.presentation.navigation

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.posse.kotlin1.calendar.feature_calendar.my_calendar.presentation.MyCalendarViewModel
import com.posse.kotlin1.calendar.feature_calendar.presentation.CalendarView

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun Navigation(
    navController: NavHostController,
    paddingValues: PaddingValues,
    dataReadyCallback: (needDelay: Boolean) -> Unit
) {
    AnimatedNavHost(
        navController = navController,
        startDestination = BottomNavigation.Calendar.destination,
        route = MAIN_ROUTE,
        modifier = Modifier
            .background(color = MaterialTheme.colorScheme.background)
            .padding(paddingValues)
    ) {

        composable(
            route = BottomNavigation.Calendar.destination
        ) {
            LaunchedEffect(key1 = true) { dataReadyCallback(true) }
            CalendarView(hiltViewModel<MyCalendarViewModel>())
        }
    }
}

private const val MAIN_ROUTE = "main_route"
const val ANIMATION_DURATION = 500
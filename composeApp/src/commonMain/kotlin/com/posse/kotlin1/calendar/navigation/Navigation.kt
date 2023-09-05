package com.posse.kotlin1.calendar.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.posse.kotlin1.calendar.common.compose.components.BackButtonExitApp
import com.posse.kotlin1.calendar.common.compose.utils.ANIMATION_DURATION
import com.posse.kotlin1.calendar.common.compose.utils.MultipleEventsCutter
import com.posse.kotlin1.calendar.common.compose.utils.SlideDirection
import com.posse.kotlin1.calendar.common.compose.utils.get
import com.posse.kotlin1.calendar.common.compose.utils.getNavTransition
import com.posse.kotlin1.calendar.common.theme.AppTheme
import moe.tlaster.precompose.navigation.NavHost
import moe.tlaster.precompose.navigation.rememberNavigator

@Composable
fun Navigation(
    showError: Composable.() -> Unit
) {
    val navigator = rememberNavigator()
    val multipleEventsCutter = remember { MultipleEventsCutter.get(ANIMATION_DURATION + 200L) }

    var targetRoute: String? by remember { mutableStateOf(null) }
    var previousRoute: String? by remember { mutableStateOf(null) }

    LaunchedEffect(true) {
        navigator.currentEntry.collect { backstack ->
            previousRoute = targetRoute
            targetRoute = backstack?.route?.route
        }
    }

    AppTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            NavHost(
                navigator = navigator,
                initialRoute = NavigationTree.Main.name,
                modifier = Modifier.fillMaxSize()
            ) {
                scene(
                    route = NavigationTree.Main.name,
                    navTransition = getNavTransition(
                        createDirection = {
                            if (previousRoute != null) SlideDirection.Right
                            else SlideDirection.Left
                        },
                        destroyDirection = {
                            if (previousRoute != null) SlideDirection.Left
                            else SlideDirection.Right
                        }
                    ),
                ) {
//                    val viewModel = viewModel(modelClass = MyCalendarViewModel::class) { MyCalendarViewModel() }
//                    val state by viewModel.viewStates().collectAsStateWithLifecycle()
//                    val splashAction by viewModel.viewActions().collectAsStateWithLifecycle()
//
//                    MyCalendar(
//                        toAccountFlow = remember {
//                            {
//                                multipleEventsCutter.processEvent {
//                                    navigator.navigate(
//                                        NavigationTree.MainFlow.AccountFlow.name,
//                                        options = NavOptions(launchSingleTop = true)
//                                    )
//                                }
//                            }
//                        }
//                    )
                }

//                scene(route = NavigationTree.MainFlow.AccountFlow.name) {
//                    val viewModel = viewModel(modelClass = AccountNavigationViewModel::class) {
//                        AccountNavigationViewModel()
//                    }
//                    val state by viewModel.viewStates().collectAsStateWithLifecycle()
//                    val splashAction by viewModel.viewActions().collectAsStateWithLifecycle()
//
//                    AccountNavigationScreen(
//                        state = { state },
//                        viewAction = { splashAction },
//                        obtainEvent = viewModel::obtainEvent,
//                        logout = remember {
//                            {
//                                multipleEventsCutter.processEvent {
//                                    navigator.navigate(
//                                        route = NavigationTree.MainFlow.AuthFlow.name,
//                                        options = NavOptions(launchSingleTop = true)
//                                    )
//                                }
//                            }
//                        }
//                    )
//                }
            }

            BackButtonExitApp { targetRoute == NavigationTree.Main.name }
        }
    }
}
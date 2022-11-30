package com.posse.kotlin1.calendar.common.presentation.navigation

import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScaffoldBottomNavigation(
    navController: NavHostController,
    dataReadyCallback: (needDelay: Boolean) -> Unit
) {
    val navItems = remember {
        listOf(
            BottomNavigation.Calendar,
            BottomNavigation.Friends,
            BottomNavigation.Settings
        )
    }

    Scaffold(
        bottomBar = {
            BottomNavigation(
                backgroundColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = contentColorFor(MaterialTheme.colorScheme.onPrimaryContainer),
            ) {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                navItems.forEach { screen ->
                    BottomNavigationItem(
                        icon = { Icon(screen.icon, contentDescription = null) },
                        label = { Text(stringResource(screen.resourceId)) },
                        selected = currentDestination?.hierarchy?.any { it.route == screen.destination } == true,
                        onClick = {
                            navController.navigate(screen.destination) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        Navigation(
            navController = navController,
            paddingValues = innerPadding,
            dataReadyCallback = dataReadyCallback
        )
    }
}
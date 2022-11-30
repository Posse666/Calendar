package com.posse.kotlin1.calendar.common.presentation.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.PermContactCalendar
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import com.posse.kotlin1.calendar.R

sealed class BottomNavigation(
    val destination: String,
    @StringRes val resourceId: Int,
    val icon: ImageVector
) {
    object Calendar : BottomNavigation(
        destination = "calendar_screen",
        resourceId = R.string.calendar_bottom,
        icon = Icons.Default.CalendarMonth
    )

    object Friends : BottomNavigation(
        destination = "friends_screen",
        resourceId = R.string.friends_bottom,
        icon = Icons.Default.PermContactCalendar
    )

    object Settings : BottomNavigation(
        destination = "settings_screen",
        resourceId = R.string.settings_bottom,
        icon = Icons.Default.Settings
    )
}
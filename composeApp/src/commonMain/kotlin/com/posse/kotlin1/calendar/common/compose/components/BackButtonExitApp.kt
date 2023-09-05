package com.posse.kotlin1.calendar.common.compose.components

import androidx.compose.runtime.Composable

@Composable
expect fun BackButtonExitApp(enabled: () -> Boolean = { true })
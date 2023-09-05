package com.posse.kotlin1.calendar.common.compose.components

import androidx.compose.runtime.Composable

@Composable
actual fun AndroidBackButtonHandler(
    enabled: Boolean,
    onBackRequest: () -> Unit
) = Unit
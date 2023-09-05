package com.posse.kotlin1.calendar.common.compose.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.posse.kotlin1.calendar.common.compose.utils.ANIMATION_DURATION
import com.posse.kotlin1.calendar.common.compose.utils.getStandardAnimation
import kotlinx.coroutines.delay

@Composable
fun <T> AnimatedVisibilityDialog(
    dialogData: () -> T,
    enterTransition: EnterTransition = fadeIn(getStandardAnimation()),
    exitTransition: ExitTransition = fadeOut(getStandardAnimation()),
    dialog: @Composable (T) -> Unit
) {
    val data by remember { derivedStateOf(dialogData) }
    var isVisible by remember { mutableStateOf(data != null) }

    var currentData by remember { mutableStateOf(data) }

    LaunchedEffect(key1 = data) {
        if (data == currentData) return@LaunchedEffect
        isVisible = false
        delay(ANIMATION_DURATION.toLong())
        currentData = data
        isVisible = true
    }

    AnimatedVisibility(
        visible = isVisible,
        enter = enterTransition,
        exit = exitTransition
    ) {
        dialog(currentData)
    }
}
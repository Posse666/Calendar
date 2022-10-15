package com.posse.kotlin1.calendar.common.presentation

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import com.google.accompanist.systemuicontroller.rememberSystemUiController

private val BlackScrim = Color(0f, 0f, 0f, 0.3f) // 30% opaque black

@Composable
fun StatusBarColor(
    isTransparent: Boolean = true
) {
    val systemUiController = rememberSystemUiController()
    val useDarkIcons = !isSystemInDarkTheme()
    val backgroundColor = MaterialTheme.colorScheme.background

    val alpha by animateFloatAsState(
        if (isTransparent) 0f else 1f,
        tween(300)
    )

    SideEffect {
        systemUiController.setSystemBarsColor(
            color = backgroundColor.copy(alpha = alpha),
            darkIcons = useDarkIcons,
            isNavigationBarContrastEnforced = false,
            transformColorForLightContent = { original -> BlackScrim.compositeOver(original) }
        )
    }
}
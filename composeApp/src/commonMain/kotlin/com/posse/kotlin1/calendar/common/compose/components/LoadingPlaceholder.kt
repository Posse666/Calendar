package com.posse.kotlin1.calendar.common.compose.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.posse.kotlin1.calendar.common.compose.utils.getStandardAnimation

@Composable
fun LoadingPlaceholder(
    isShowing: () -> Boolean,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = isShowing(),
        enter = fadeIn(getStandardAnimation()),
        exit = fadeOut(getStandardAnimation()),
        modifier = modifier
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxSize()
                .clickable(
                    enabled = isShowing(),
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = {}
                )
                .background(MaterialTheme.colorScheme.background.copy(alpha = 0.7f))
        ) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
        }
    }
}
package com.posse.kotlin1.calendar.common.compose.utils

import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.debugInspectorInfo
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp

@Composable
fun Modifier.navigationBarsPadding(): Modifier =
    then(Modifier.padding(WindowInsets.navigationBars.asPaddingValues()))

@Composable
fun Modifier.imePadding(): Modifier = then(Modifier.padding(WindowInsets.ime.asPaddingValues()))

fun Modifier.clickableSingle(
    enabled: Boolean = true,
    onClickLabel: String? = null,
    role: Role? = null,
    onClick: () -> Unit
) = composed(
    inspectorInfo = debugInspectorInfo {
        name = "clickable"
        properties["enabled"] = enabled
        properties["onClickLabel"] = onClickLabel
        properties["role"] = role
        properties["onClick"] = onClick
    }
) {
    val multipleEventsCutter = remember { MultipleEventsCutter.get() }
    Modifier.clickable(
        enabled = enabled,
        onClickLabel = onClickLabel,
        onClick = { multipleEventsCutter.processEvent { onClick() } },
        role = role,
        indication = LocalIndication.current,
        interactionSource = remember { MutableInteractionSource() }
    )
}

fun Modifier.exceptBottomPadding(paddingValues: PaddingValues) = composed {
    val layoutDirection = LocalLayoutDirection.current

    val newPaddings = PaddingValues(
        top = paddingValues.calculateTopPadding(),
        bottom = 0.dp,
        end = paddingValues.calculateEndPadding(layoutDirection),
        start = paddingValues.calculateStartPadding(layoutDirection)
    )

    Modifier.padding(newPaddings)
}

fun Modifier.exceptTopPadding(paddingValues: PaddingValues) = composed {
    val layoutDirection = LocalLayoutDirection.current

    val newPaddings = PaddingValues(
        top = 0.dp,
        bottom = paddingValues.calculateBottomPadding(),
        end = paddingValues.calculateEndPadding(layoutDirection),
        start = paddingValues.calculateStartPadding(layoutDirection)
    )

    Modifier.padding(newPaddings)
}

fun Modifier.onlyHorizontalPaddings(paddingValues: PaddingValues) = composed {
    val layoutDirection = LocalLayoutDirection.current

    val newPaddings = PaddingValues(
        top = 0.dp,
        bottom = 0.dp,
        end = paddingValues.calculateEndPadding(layoutDirection),
        start = paddingValues.calculateStartPadding(layoutDirection)
    )

    Modifier.padding(newPaddings)
}
package com.posse.kotlin1.calendar.common.compose.utils

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.waterfall
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp

@Composable
fun navBarBottomPaddings(): Int = WindowInsets.navigationBars.getBottom(LocalDensity.current)

@Composable
fun getSystemPaddings(): PaddingValues = WindowInsets.systemBars.asPaddingValues() + WindowInsets.waterfall.asPaddingValues()

@Composable
fun getTextPadding(textStyle: TextStyle = MaterialTheme.typography.bodyMedium): Dp {
    return textStyle.lineHeight.value.dp / 2
}

val verticalArrangementWithLastElementAtBottom: Arrangement.Vertical
    get() = object : Arrangement.Vertical {
        override fun Density.arrange(
            totalSize: Int,
            sizes: IntArray,
            outPositions: IntArray
        ) {
            var currentOffset = 0
            sizes.forEachIndexed { index, size ->
                if (index == sizes.lastIndex) {
                    outPositions[index] = totalSize - size
                } else {
                    outPositions[index] = currentOffset
                    currentOffset += size
                }
            }
        }
    }

private operator fun PaddingValues.plus(paddingValues: PaddingValues): PaddingValues {
    return PaddingValues(
        bottom = calculateBottomPadding() + paddingValues.calculateBottomPadding(),
        top = calculateTopPadding() + paddingValues.calculateTopPadding(),
        start = calculateStartPadding(LayoutDirection.Ltr)
                + paddingValues.calculateStartPadding(LayoutDirection.Ltr),
        end = calculateEndPadding(LayoutDirection.Ltr)
                + paddingValues.calculateEndPadding(LayoutDirection.Ltr)
    )
}
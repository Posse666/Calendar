package com.posse.kotlin1.calendar.common.compose.components

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalMinimumInteractiveComponentEnforcement
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShadowBox(
    modifier: Modifier = Modifier,
    elevationProvider: () -> Dp,
    shape: Shape = RectangleShape,
    content: @Composable () -> Unit
) {
    Layout(
        {
            ClippedShadow(
                elevationProvider = elevationProvider,
                shape = shape,
                modifier = modifier
            )
            CompositionLocalProvider(LocalMinimumInteractiveComponentEnforcement provides false) {
                content()
            }
        },
        modifier
    ) { measurables, constraints ->
        require(measurables.size == 2)

        val shadow = measurables[0]
        val target = measurables[1]

        val targetPlaceable = target.measure(constraints)
        val width = targetPlaceable.width
        val height = targetPlaceable.height

        val shadowPlaceable = shadow.measure(Constraints.fixed(width, height))

        layout(width, height) {
            shadowPlaceable.place(0, 0)
            targetPlaceable.place(0, 0)
        }
    }
}

@Composable
private fun ClippedShadow(
    elevationProvider: () -> Dp,
    shape: Shape,
    modifier: Modifier = Modifier
) {
    Layout(
        modifier
            .drawWithCache {
                // Naive cache setup similar to foundation's Background.
                val path = Path()
                var lastSize: Size? = null

                fun updatePathIfNeeded() {
                    if (size != lastSize) {
                        path.reset()
                        path.addOutline(
                            shape.createOutline(size, layoutDirection, this)
                        )
                        lastSize = size
                    }
                }

                onDrawWithContent {
                    updatePathIfNeeded()
                    clipPath(path, ClipOp.Difference) {
                        this@onDrawWithContent.drawContent()
                    }
                }
            }
            .shadow(
                elevation = elevationProvider(),
                shape = shape,
                ambientColor = MaterialTheme.colorScheme.scrim,
                spotColor = MaterialTheme.colorScheme.scrim
            )
    ) { _, constraints ->
        layout(constraints.minWidth, constraints.minHeight) {}
    }
}
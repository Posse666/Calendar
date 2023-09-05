package com.posse.kotlin1.calendar.common.compose.utils

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.slideIn
import androidx.compose.animation.slideOut
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import moe.tlaster.precompose.navigation.transition.NavTransition

fun getNavTransition(
    createDirection: () -> SlideDirection,
    destroyDirection: () -> SlideDirection,
    pauseDirection: () -> SlideDirection = createDirection,
    resumeDirection: () -> SlideDirection = destroyDirection
): NavTransition {
    return object : NavTransition {
        override val createTransition: EnterTransition
            get() = slideIntoContainer(createDirection())
        override val destroyTransition: ExitTransition
            get() = slideOutOfContainer(destroyDirection())
        override val pauseTransition: ExitTransition
            get() = slideOutOfContainer(pauseDirection())
        override val resumeTransition: EnterTransition
            get() = slideIntoContainer(resumeDirection())
    }
}

private fun slideIntoContainer(direction: SlideDirection): EnterTransition {
    return slideIn(
        animationSpec = getStandardAnimation()
    ) { size ->
        calculateOffset(direction, size)
    }
}

private fun slideOutOfContainer(direction: SlideDirection): ExitTransition {
    return slideOut(
        animationSpec = getStandardAnimation()
    ) { size ->
        calculateOffset(direction, size, false)
    }
}

private fun calculateOffset(
    direction: SlideDirection,
    size: IntSize,
    slideIn: Boolean = true
): IntOffset {
    return when (direction) {
        SlideDirection.Right -> IntOffset(x = size.width.let { if (slideIn) -it else it }, y = 0)
        SlideDirection.Left -> IntOffset(x = size.width.let { if (slideIn) it else -it }, y = 0)
        SlideDirection.Up -> IntOffset(x = 0, y = size.height.let { if (slideIn) it else -it })
        SlideDirection.Down -> IntOffset(x = 0, y = size.height.let { if (slideIn) -it else it })
    }
}

enum class SlideDirection {
    Right, Left, Up, Down
}
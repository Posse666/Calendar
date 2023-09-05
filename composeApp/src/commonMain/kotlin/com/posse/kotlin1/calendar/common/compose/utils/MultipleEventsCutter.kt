package com.posse.kotlin1.calendar.common.compose.utils

import kotlinx.datetime.Clock

interface MultipleEventsCutter {
    fun processEvent(event: () -> Unit)

    companion object {
        const val CLICK_DURATION = 700L
    }
}

fun MultipleEventsCutter.Companion.get(duration: Long = CLICK_DURATION): MultipleEventsCutter =
    MultipleEventsCutterImpl(duration)

private class MultipleEventsCutterImpl(private val duration: Long) : MultipleEventsCutter {

    private val now: Long
        get() = Clock.System.now().toEpochMilliseconds()

    private var lastEventTimeMs: Long = 0

    override fun processEvent(event: () -> Unit) {
        if (now - lastEventTimeMs >= duration) {
            event()
            lastEventTimeMs = now
        }
    }
}
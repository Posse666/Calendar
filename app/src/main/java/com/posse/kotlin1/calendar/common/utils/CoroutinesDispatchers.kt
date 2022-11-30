package com.posse.kotlin1.calendar.common.utils

import kotlinx.coroutines.CoroutineDispatcher

interface CoroutinesDispatchers {
    val main: CoroutineDispatcher
    val io: CoroutineDispatcher
    val default: CoroutineDispatcher
    val unconfined: CoroutineDispatcher
}
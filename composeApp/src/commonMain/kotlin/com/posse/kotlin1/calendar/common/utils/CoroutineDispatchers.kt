package com.posse.kotlin1.calendar.common.utils

import kotlinx.coroutines.CoroutineDispatcher

interface CoroutineDispatchers {
    val main: CoroutineDispatcher
    val io: CoroutineDispatcher
    val unconfined: CoroutineDispatcher
    val default: CoroutineDispatcher
}
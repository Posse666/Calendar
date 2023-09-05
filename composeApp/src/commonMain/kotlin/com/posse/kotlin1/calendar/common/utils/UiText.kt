package com.posse.kotlin1.calendar.common.utils

import androidx.compose.runtime.Composable
import dev.icerock.moko.resources.StringResource
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.format

sealed class UiText {
    data class RawString(val value: String) : UiText()
    class StringResourceText(
        val resId: StringResource,
        vararg val args: Any
    ) : UiText()

    @Composable
    fun getString(): String = when (this) {
        is RawString -> value
        is StringResourceText -> resId.format(args).localized()
    }
}
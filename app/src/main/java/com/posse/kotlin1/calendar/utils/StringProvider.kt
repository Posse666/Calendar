package com.posse.kotlin1.calendar.utils

import android.content.Context
import androidx.annotation.StringRes
import javax.inject.Inject

class StringProvider @Inject constructor(private val context: Context) {
    fun getString(@StringRes id: Int): String = context.getString(id)
}
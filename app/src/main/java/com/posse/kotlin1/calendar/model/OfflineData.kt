package com.posse.kotlin1.calendar.model

import android.content.Context
import android.content.SharedPreferences

private const val KEY = "Calendar preferences"
private const val KEY_TEMPERATURE = "Calendar temperature"

class OfflineData(
    context: Context,
    private val PrefsData: SharedPreferences? = context.getSharedPreferences(
        KEY, Context.MODE_PRIVATE
    )
)

var SharedPreferences.temperature: Int
    get() = this.getInt(KEY_TEMPERATURE, 0)
    set(value) {
        this.edit()
            .putInt(KEY_TEMPERATURE, value)
            .apply()
    }
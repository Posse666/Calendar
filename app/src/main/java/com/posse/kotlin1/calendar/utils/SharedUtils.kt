package com.posse.kotlin1.calendar.utils

import android.content.SharedPreferences

var SharedPreferences.themeSwitch: Boolean
    get() = this.getBoolean("themeSwitch", true)
    set(value) {
        this.edit()
            .putBoolean("themeSwitch", value)
            .apply()
    }

var SharedPreferences.lightTheme: Boolean
    get() = this.getBoolean("lightTheme", true)
    set(value) {
        this.edit()
            .putBoolean("lightTheme", value)
            .apply()
    }
package com.posse.kotlin1.calendar.utils

import android.content.SharedPreferences

var SharedPreferences.statsUsed: Boolean
    get() = this.getBoolean("statsUsed", false)
    set(value) {
        this.edit()
            .putBoolean("statsUsed", value)
            .apply()
    }

var SharedPreferences.nickName: String?
    get() = this.getString("nickName", null)
    set(value) {
        this.edit()
            .putString("nickName", value)
            .apply()
    }

var SharedPreferences.email: String?
    get() = this.getString("email", null)
    set(value) {
        this.edit()
            .putString("email", value)
            .apply()
    }

var SharedPreferences.token: String?
    get() = this.getString("token", null)
    set(value) {
        this.edit()
            .putString("token", value)
            .apply()
    }
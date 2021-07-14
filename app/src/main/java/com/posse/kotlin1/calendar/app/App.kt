package com.posse.kotlin1.calendar.app

import android.app.Application
import android.content.Context
import android.content.SharedPreferences

class App : Application() {
    private val prefsName = "Calendar"

    override fun onCreate() {
        super.onCreate()
        appInstance = this
        sharedPreferences = this.getSharedPreferences(prefsName,Context.MODE_PRIVATE)
    }

    companion object {

        var appInstance: App? = null
        var sharedPreferences: SharedPreferences? = null
    }
}
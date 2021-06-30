package com.posse.kotlin1.calendar.app

import android.app.Application

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        appInstance = this
    }

    companion object {

        var appInstance: App? = null
    }
}
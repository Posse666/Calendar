package com.posse.kotlin1.calendar

import android.app.Application
import android.os.Bundle

import moe.tlaster.precompose.lifecycle.PreComposeActivity
import moe.tlaster.precompose.lifecycle.setContent

class AndroidApp : Application() {

    override fun onCreate() {
        super.onCreate()

    }
}

class AppActivity : PreComposeActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { App() }
    }
}
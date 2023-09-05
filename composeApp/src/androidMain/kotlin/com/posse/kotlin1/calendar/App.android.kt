package com.posse.kotlin1.calendar

import android.app.Application
import android.os.Build
import android.os.Bundle
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.view.WindowCompat
import com.posse.kotlin1.calendar.common.di.PlatformConfiguration
import com.posse.kotlin1.calendar.common.di.PlatformSDK
import com.posse.kotlin1.calendar.navigation.AppScreen
import moe.tlaster.precompose.lifecycle.PreComposeActivity
import moe.tlaster.precompose.lifecycle.setContent

class AndroidApp : Application() {
    override fun onCreate() {
        super.onCreate()
        initPlatformSDK()
    }
}

class AppActivity : PreComposeActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            splashScreen.setOnExitAnimationListener { it.remove() }
        }

        setupThemedNavigation()
    }
}

fun PreComposeActivity.setupThemedNavigation() {

    WindowCompat.setDecorFitsSystemWindows(window, false)
    window.statusBarColor = Color.Transparent.toArgb()
    window.navigationBarColor = Color.Transparent.toArgb()

    setContent {
        AppScreen()
    }
}

fun AndroidApp.initPlatformSDK() = PlatformSDK.init(
    configuration = PlatformConfiguration(androidContext = applicationContext)
)
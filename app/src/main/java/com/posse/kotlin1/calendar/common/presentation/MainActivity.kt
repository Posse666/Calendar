package com.posse.kotlin1.calendar.common.presentation

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.rememberCoroutineScope
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.posse.kotlin1.calendar.app_theme.AppTheme
import com.posse.kotlin1.calendar.common.presentation.navigation.ANIMATION_DURATION
import com.posse.kotlin1.calendar.common.presentation.navigation.ScaffoldBottomNavigation
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private var isContentReady = false
    private var splashDelay = true

    @OptIn(ExperimentalAnimationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        installSplashScreen().apply {
            CoroutineScope(Dispatchers.IO).launch {
                delay(1_000)
                splashDelay = false
            }
            setKeepOnScreenCondition { splashDelay || !isContentReady }
        }

        setContent {
            val coroutineScope = rememberCoroutineScope()
            val navController = rememberAnimatedNavController()

            StatusBarColor(false)

            AppTheme {
                ScaffoldBottomNavigation(
                    navController = navController,
                    dataReadyCallback = { delay ->
                        coroutineScope.launch {
                            if (!isContentReady) {
                                if (delay) delay(ANIMATION_DURATION.toLong())
                                isContentReady = true
                            }
                        }
                    }
                )
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putBoolean(CONTENT_KEY, isContentReady)
        outState.putBoolean(SPLASH_DELAY_KEY, splashDelay)
        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        isContentReady = savedInstanceState.getBoolean(CONTENT_KEY)
        splashDelay = savedInstanceState.getBoolean(SPLASH_DELAY_KEY)
        super.onRestoreInstanceState(savedInstanceState)
    }

    companion object {
        private const val CONTENT_KEY = "content_key"
        private const val SPLASH_DELAY_KEY = "splash_delay_key"
    }
}
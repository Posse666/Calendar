package com.posse.kotlin1.calendar.common.compose.components

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import dev.icerock.moko.resources.desc.desc
import kotlin.system.exitProcess
import resources.MR

@Composable
actual fun BackButtonExitApp(enabled: () -> Boolean) {
    val context = LocalContext.current

    var lastTimeBackPressed = remember { 0L }

    var isBackShown = remember { false }

    BackHandler(enabled()) {
        if (System.currentTimeMillis() - lastTimeBackPressed < BACK_BUTTON_EXIT_DELAY && isBackShown) {
            exitProcess(0)
        } else isBackShown = false
        Toast
            .makeText(
                context,
                MR.strings.back_again_to_exit.desc().toString(context),
                Toast.LENGTH_SHORT
            )
            .show()
        isBackShown = true
        lastTimeBackPressed = System.currentTimeMillis()
    }
}

private const val BACK_BUTTON_EXIT_DELAY = 3000
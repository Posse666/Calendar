package com.posse.kotlin1.calendar.utils

import android.content.res.Configuration
import android.os.Build
import androidx.annotation.IdRes
import androidx.annotation.StyleRes
import com.posse.kotlin1.calendar.R
import com.posse.kotlin1.calendar.app.App
import com.posse.kotlin1.calendar.view.settings.NIGHT_THEME_SDK

fun getAppTheme(): Int {
    @StyleRes
    var theme: Int = THEME.DAY.themeID

    if (Build.VERSION.SDK_INT >= NIGHT_THEME_SDK) {
        if (App.sharedPreferences.themeSwitch){
            when (App.appInstance.resources?.configuration?.uiMode?.and(Configuration.UI_MODE_NIGHT_MASK)) {
                Configuration.UI_MODE_NIGHT_YES -> theme = THEME.NIGHT.themeID
            }
            return theme
        }
    }
    if (!App.sharedPreferences.lightTheme){
        theme = THEME.NIGHT.themeID
    }
    return theme
}

enum class THEME(@IdRes val resID: Int, @StyleRes val themeID: Int) {
    DAY(R.id.chipDay, R.style.Theme_AlcoCalendar),
    NIGHT(R.id.chipNight, R.style.Theme_AlcoCalendar_Night)
}
package com.posse.kotlin1.calendar.utils

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Build
import androidx.annotation.IdRes
import androidx.annotation.StyleRes
import com.posse.kotlin1.calendar.R
import com.posse.kotlin1.calendar.view.settings.SettingsFragment.Companion.NIGHT_THEME_SDK
import javax.inject.Inject

class ThemeUtils @Inject constructor(
    private val sharedPreferences: SharedPreferences,
    private val context: Context
) {

    fun getAppTheme(): Int {
        @StyleRes
        var theme: Int = THEME.DAY.themeID

        if (Build.VERSION.SDK_INT >= NIGHT_THEME_SDK) {
            if (sharedPreferences.themeSwitch) {
                if (context.resources?.configuration?.uiMode?.and(Configuration.UI_MODE_NIGHT_MASK)
                    == Configuration.UI_MODE_NIGHT_YES
                ) theme = THEME.NIGHT.themeID
                return theme
            }
        }
        if (!sharedPreferences.lightTheme) {
            theme = THEME.NIGHT.themeID
        }
        return theme
    }

    enum class THEME(@IdRes val resID: Int, @StyleRes val themeID: Int) {
        DAY(R.id.chipDay, R.style.Theme_AlcoCalendar),
        NIGHT(R.id.chipNight, R.style.Theme_AlcoCalendar_Night)
    }
}
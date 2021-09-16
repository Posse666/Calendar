package com.posse.kotlin1.calendar.utils

import android.content.res.Configuration
import android.content.res.Resources
import com.posse.kotlin1.calendar.app.App
import com.posse.kotlin1.calendar.view.MainActivity
import java.util.*

const val LOCALE_EN = "En"
const val LOCALE_RU = "Ru"

fun getStringLocale(): String = when (Locale.getDefault().language) {
    LOCALE.RU.locale.language -> LOCALE.RU.value
    else -> LOCALE.DEFAULT.value
}

fun getLocale(locale: String): Locale {
    var result = LOCALE.DEFAULT.locale
    LOCALE.values().forEach {
        if (it.value.lowercase() == locale.lowercase()) result = it.locale
    }
    return result
}

fun setAppLocale(stringLocale: String?) {
    var newStringLocale = stringLocale
    if (newStringLocale == null) {
        newStringLocale = getStringLocale()
    }
    App.sharedPreferences.locale = newStringLocale
    val locale = getLocale(newStringLocale)
    Locale.setDefault(locale)
    val resources: Resources = MainActivity.instance.resources
    val config: Configuration = resources.configuration
    config.setLocale(locale)
    resources.updateConfiguration(config, resources.displayMetrics)
}

enum class LOCALE(val locale: Locale, val value: String) {
    DEFAULT(Locale(LOCALE_EN.lowercase()), LOCALE_EN),
    RU(Locale(LOCALE_RU.lowercase()), LOCALE_RU)
}
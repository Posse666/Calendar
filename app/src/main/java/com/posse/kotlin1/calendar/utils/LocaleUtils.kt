package com.posse.kotlin1.calendar.utils

import android.content.SharedPreferences
import android.content.res.Configuration
import android.content.res.Resources
import com.posse.kotlin1.calendar.view.MainActivity
import java.util.*
import javax.inject.Inject

class LocaleUtils @Inject constructor(private val sharedPreferences: SharedPreferences) {

    fun getStringLocale(): String = when (Locale.getDefault().language) {
        LOCALE.Ru.locale.language -> LOCALE.Ru.value
        else -> LOCALE.Default.value
    }

    private fun getLocale(locale: String): Locale {
        var result = LOCALE.Default.locale
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
        sharedPreferences.locale = newStringLocale
        val locale = getLocale(newStringLocale)
        Locale.setDefault(locale)
        val resources: Resources = MainActivity.instance.resources
        val config: Configuration = resources.configuration
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)
    }

    enum class LOCALE(val locale: Locale, val value: String) {
        Default(Locale(LOCALE_EN.lowercase()), LOCALE_EN),
        Ru(Locale(LOCALE_RU.lowercase()), LOCALE_RU)
    }

    companion object {
        const val LOCALE_EN = "En"
        const val LOCALE_RU = "Ru"
    }
}
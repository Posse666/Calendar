package com.posse.kotlin1.calendar.utils

import java.util.*

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

enum class LOCALE(val locale: Locale, val value: String) {
    DEFAULT(Locale("en"), "En"),
    RU(Locale("ru"), "Ru")
}
package com.posse.kotlin1.calendar.utils

import com.google.gson.reflect.TypeToken
import com.google.gson.Gson
import java.time.LocalDate

fun convertLocalDateToLong(date: LocalDate): Long {
    return date.toEpochDay()
}

fun convertLongToLocalDale(date: Long): LocalDate {
    return LocalDate.ofEpochDay(date)
}

inline fun <reified T> Map<String, Any>.toDataClass(): T {
    return convert()
}

inline fun <I, reified O> I.convert(): O {
    val gson = Gson()
    val json = gson.toJson(this)
    return gson.fromJson(json, object : TypeToken<O>() {}.type)
}
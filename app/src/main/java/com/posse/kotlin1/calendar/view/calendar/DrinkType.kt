package com.posse.kotlin1.calendar.view.calendar

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
enum class DrinkType(val value: String) : Parcelable {
    Full("Full"),
    Half("Half")
}
package com.posse.kotlin1.calendar.common.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
enum class DrinkType(val value: String) : Parcelable {
    Full("Full"),
    Half("Half")
}
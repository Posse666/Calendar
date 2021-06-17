package com.posse.kotlin1.calendar.view.settings.share

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Contact(val name: String, val email: String) : Parcelable

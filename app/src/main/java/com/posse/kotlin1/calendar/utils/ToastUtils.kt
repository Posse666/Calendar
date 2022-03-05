package com.posse.kotlin1.calendar.utils

import android.content.Context
import android.view.Gravity
import android.widget.TextView
import android.widget.Toast

fun Context.showToast(text: String) {
    val toast = Toast.makeText(this, text, Toast.LENGTH_SHORT)

    @Suppress("DEPRECATION")
    toast.view?.findViewById<TextView>(android.R.id.message)?.apply { gravity = Gravity.CENTER }
    toast.show()
}
package com.posse.kotlin1.calendar.utils

import android.content.Context
import android.view.Gravity
import android.widget.TextView
import android.widget.Toast
import com.posse.kotlin1.calendar.R

fun Context.showOfflineToast() {
    val toast = Toast.makeText(this, this.getString(R.string.no_connection), Toast.LENGTH_SHORT)
    val v = toast.view?.findViewById<TextView>(android.R.id.message)
    if (v != null) v.gravity = Gravity.CENTER
    toast.show()
}
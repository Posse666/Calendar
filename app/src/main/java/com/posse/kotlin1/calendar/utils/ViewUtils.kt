package com.posse.kotlin1.calendar.utils

import android.view.View
import android.widget.TextView

fun TextView.putText(newValue: Any) {
    text = newValue.toString()
}

fun View.show() {
    visibility = View.VISIBLE
}

fun View.disappear() {
    visibility = View.GONE
}

fun View.hide() {
    visibility = View.INVISIBLE
}
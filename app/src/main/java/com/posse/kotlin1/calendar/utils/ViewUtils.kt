package com.posse.kotlin1.calendar.utils

import android.view.View
import android.widget.TextView
import com.google.android.material.textfield.TextInputLayout

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

fun TextInputLayout.enable() {
    this.isEnabled = true
    this.editText?.requestFocus()
}

fun TextInputLayout.disable() {
    this.isEnabled = false
    this.editText?.clearFocus()
}
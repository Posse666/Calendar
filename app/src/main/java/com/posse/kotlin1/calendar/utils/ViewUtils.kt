package com.posse.kotlin1.calendar.utils

import android.content.Context
import android.view.View
import android.widget.LinearLayout
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

fun LinearLayout.add(context: Context, text: String) {
    val textView = TextView(context)
    textView.putText(text)
    addView(textView)
}

fun View.setFocus() {
    focusable = View.FOCUSABLE
    isFocusableInTouchMode = true
    isClickable = true
    requestFocus()
}

fun View.removeFocus() {
    focusable = View.NOT_FOCUSABLE
    isFocusableInTouchMode = false
    isClickable = false
    clearFocus()
}
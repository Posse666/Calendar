package com.posse.kotlin1.calendar.utils

import android.app.Activity
import android.graphics.Rect
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.posse.kotlin1.calendar.view.MainActivity

class Keyboard {
    private var isKeyboardOpened: Boolean = false

    fun show() {
        val inputMethodManager =
            MainActivity.instance!!.getSystemService(android.content.Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val rootView: View = MainActivity.instance!!.window.decorView.rootView
        rootView.viewTreeObserver.addOnGlobalLayoutListener {
            val r = Rect()
            rootView.getWindowVisibleDisplayFrame(r)
            val screenHeight = rootView.height
            val keypadHeight = screenHeight - r.bottom
            isKeyboardOpened = keypadHeight > screenHeight * 0.2
        }
        if (!isKeyboardOpened) {
            inputMethodManager.toggleSoftInput(
                InputMethodManager.SHOW_FORCED,
                0
            )
        }
    }

    fun hide( view: View) {
        val inputMethodManager =
            MainActivity.instance!!.getSystemService(android.content.Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }
}
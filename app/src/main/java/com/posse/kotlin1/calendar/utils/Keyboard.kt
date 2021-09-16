package com.posse.kotlin1.calendar.utils

import android.graphics.Rect
import android.view.View
import android.view.ViewTreeObserver
import android.view.inputmethod.InputMethodManager
import com.posse.kotlin1.calendar.view.MainActivity

class Keyboard {
    private var isKeyboardOpened: Boolean = false
    private var listener: KeyboardListener? = null
    private val inputMethodManager =
        MainActivity.instance.getSystemService(android.content.Context.INPUT_METHOD_SERVICE) as InputMethodManager
    private val rootView: View = MainActivity.instance.window.decorView.rootView
    private val globalListener = ViewTreeObserver.OnGlobalLayoutListener {
        val r = Rect()
        rootView.getWindowVisibleDisplayFrame(r)
        val screenHeight = rootView.height
        val keypadHeight = screenHeight - r.bottom
        val tempKeyboardOpened = keypadHeight > screenHeight * 0.2
        if (isKeyboardOpened == !tempKeyboardOpened) {
            isKeyboardOpened = tempKeyboardOpened
            if (!isKeyboardOpened) listener?.keyboardClosed()
        }
    }

    init {
        rootView.viewTreeObserver.addOnGlobalLayoutListener(globalListener)
    }

    fun show() {
        if (!isKeyboardOpened) inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
    }

    fun hide(view: View) {
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    fun setListener(listener: KeyboardListener?) {
        this.listener = listener
    }

    fun removeGlobalListener() {
        rootView.viewTreeObserver.removeOnGlobalLayoutListener(globalListener)
    }
}

fun interface KeyboardListener {
    fun keyboardClosed()
}
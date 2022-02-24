package com.posse.kotlin1.calendar.utils

import android.app.Activity
import android.content.ContextWrapper
import android.graphics.Rect
import android.view.View
import android.view.ViewTreeObserver
import android.view.inputmethod.InputMethodManager
import androidx.core.content.getSystemService

class Keyboard {
    private var isKeyboardOpened: Boolean = false
    private var listener: KeyboardListener? = null
    private var globalListener: ViewTreeObserver.OnGlobalLayoutListener? = null

    fun setGlobalListener(rootView: View?) {
        rootView?.let {
            globalListener = ViewTreeObserver.OnGlobalLayoutListener {
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
        }
        rootView?.viewTreeObserver?.addOnGlobalLayoutListener(globalListener)
    }

    fun show(activity: Activity?) {
        if (!isKeyboardOpened) activity?.getSystemService<InputMethodManager>()?.toggleSoftInput(
            InputMethodManager.SHOW_FORCED,
            0
        )
    }

    fun hide(view: View) {
        view.getActivity()?.getSystemService<InputMethodManager>()
            ?.hideSoftInputFromWindow(view.windowToken, 0)
    }

    fun setListener(listener: KeyboardListener?) {
        this.listener = listener
    }

    fun removeGlobalListener(rootView: View?) {
        rootView?.viewTreeObserver?.removeOnGlobalLayoutListener(globalListener)
    }

    private fun View.getActivity(): Activity? {
        var context = context
        while (context is ContextWrapper) {
            if (context is Activity) {
                return context
            }
            context = context.baseContext
        }
        return null
    }
}

fun interface KeyboardListener {
    fun keyboardClosed()
}
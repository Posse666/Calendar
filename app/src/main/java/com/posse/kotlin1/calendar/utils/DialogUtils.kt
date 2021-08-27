package com.posse.kotlin1.calendar.utils

import android.view.WindowManager
import androidx.fragment.app.DialogFragment

fun setWindowSize(dialogFragment: DialogFragment, height: Int, transparent: Boolean = true) {
    val dialogWindow = dialogFragment.dialog?.window
    val params = dialogWindow?.attributes
    if (params != null) {
        params.width = WindowManager.LayoutParams.MATCH_PARENT
        params.height = height
        dialogWindow.attributes = params
        if (transparent) dialogWindow.setBackgroundDrawableResource(android.R.color.transparent)
    }
}
package com.posse.kotlin1.calendar.utils

import android.view.WindowManager
import androidx.fragment.app.DialogFragment

fun setWindowSize(dialogFragment: DialogFragment, height: Int) {
    val dialogWindow = dialogFragment.dialog?.window
    val params = dialogWindow?.attributes
    if (params != null) {
        params.width = WindowManager.LayoutParams.MATCH_PARENT
        params.height = height
        dialogWindow.attributes = params
        dialogWindow.setBackgroundDrawableResource(android.R.color.transparent)
    }
}
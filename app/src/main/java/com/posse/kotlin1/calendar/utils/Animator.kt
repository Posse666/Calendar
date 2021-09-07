package com.posse.kotlin1.calendar.utils

import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator

class Animator {

    fun animate(view: View, callback: () -> Unit) {
        view
            .animate()
            .setDuration(200)
            .scaleX(0.2f)
            .scaleY(0.2f)
            .setInterpolator(DecelerateInterpolator())
            .withEndAction {
                callback.invoke()
                view
                    .animate()
                    .setDuration(200)
                    .scaleX(1f)
                    .scaleY(1f).interpolator = AccelerateInterpolator()
            }
    }
}
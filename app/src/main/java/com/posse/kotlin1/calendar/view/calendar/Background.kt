package com.posse.kotlin1.calendar.view.calendar

import android.content.Context
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import androidx.core.content.res.ResourcesCompat
import com.posse.kotlin1.calendar.R

object Background {
    private const val PADDING: Int = 5
    private val circleSize: Int =
        Resources.getSystem().displayMetrics.widthPixels / Days.values().size

    fun getCircle(context: Context, circleType: CircleType): Drawable {
        val drawable = GradientDrawable()
        drawable.shape = GradientDrawable.OVAL
        drawable.setSize(circleSize, circleSize)
        when (circleType) {
            CircleType.EMPTY -> {
                drawable.setStroke(PADDING, getColor(context, R.color.strokeColor))
            }
            CircleType.FULL -> {
                drawable.setColor(getColor(context, R.color.fillColor))
                drawable.setStroke(PADDING, getColor(context, R.color.strokeColor))
            }
            CircleType.SELECTED_EMPTY -> {
                drawable.setStroke(PADDING, getColor(context, R.color.strokeColor_selected))
            }
            CircleType.SELECTED_FULL -> {
                drawable.setColor(getColor(context, R.color.fillColor))
                drawable.setStroke(PADDING, getColor(context, R.color.strokeColor_selected))
            }
        }
        return drawable
    }

    private fun getColor(context: Context, colorResource: Int) =
        ResourcesCompat.getColor(context.resources, colorResource, null)
}

enum class CircleType {
    EMPTY,
    FULL,
    SELECTED_EMPTY,
    SELECTED_FULL
}
package com.posse.kotlin1.calendar.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.annotation.StyleRes
import androidx.core.content.getSystemService
import com.posse.kotlin1.calendar.R
import com.posse.kotlin1.calendar.databinding.ShotGlassLayoutBinding
import com.posse.kotlin1.calendar.utils.hide
import com.posse.kotlin1.calendar.utils.putText
import com.posse.kotlin1.calendar.utils.show

class ShotGlass @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes defStyleAttr: Int = 0,
    @StyleRes defStyleRes: Int = 0
) : FrameLayout(context, attrs, defStyleAttr, defStyleRes) {

    private var _binding: ShotGlassLayoutBinding? = null
    private val binding get() = _binding!!

    init {
        _binding = ShotGlassLayoutBinding.inflate(LayoutInflater.from(context), this, true)

        attrs?.let {
            val typedArray = context.obtainStyledAttributes(it, R.styleable.ShotGlass)
            for (i in 0 until typedArray.indexCount) {
                when (typedArray.getIndex(i)) {

                    R.styleable.ShotGlass_hideInner -> {
                        val hideInner = typedArray.getBoolean(R.styleable.ShotGlass_hideInner, true)
                        hideInner(hideInner)
                    }

                    R.styleable.ShotGlass_setInnerColor -> {
                        val color = typedArray.getColor(
                            R.styleable.ShotGlass_setInnerColor,
                            resources.getColor(R.color.fillColor, null)
                        )
                        setInnerColor(color)
                    }

                    R.styleable.ShotGlass_setText -> {
                        val text = typedArray.getString(R.styleable.ShotGlass_setText)
                        setText(text)
                    }

                    R.styleable.ShotGlass_setTextColor -> {
                        val color = typedArray.getColor(
                            R.styleable.ShotGlass_setTextColor,
                            resources.getColor(R.color.strokeColor, null)
                        )
                        setTextColor(color)
                    }

                }
            }
            typedArray.recycle()
        }
    }

    fun hideInner(hide: Boolean) {
        if (hide) binding.shotGlassInner.hide()
        else binding.shotGlassInner.show()
    }

    fun setInnerColor(@ColorInt color: Int) {
        binding.shotGlassInner.setColorFilter(color)
    }

    fun setTextColor(@ColorInt color: Int) {
        binding.shotGlassText.setTextColor(color)
    }

    fun setText(text: String?) {
        if (text == null) binding.shotGlassText.hide()
        else {
            binding.shotGlassText.show()
            binding.shotGlassText.putText(text)
        }
    }
}
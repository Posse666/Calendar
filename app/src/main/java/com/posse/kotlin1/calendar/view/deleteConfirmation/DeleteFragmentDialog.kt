package com.posse.kotlin1.calendar.view.deleteConfirmation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.annotation.ColorInt
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import com.google.android.material.color.MaterialColors
import com.posse.kotlin1.calendar.R
import com.posse.kotlin1.calendar.databinding.FragmentDeleteDialogBinding
import com.posse.kotlin1.calendar.utils.Animator
import com.posse.kotlin1.calendar.utils.putText
import com.posse.kotlin1.calendar.utils.setWindowSize
import com.posse.kotlin1.calendar.utils.show

class DeleteFragmentDialog : DialogFragment() {
    private var _binding: FragmentDeleteDialogBinding? = null
    private val binding get() = _binding!!
    private lateinit var captionText: String
    private lateinit var confirmText: String
    private val animator = Animator()
    private var isBlock = false
    private var blocked = false
    private var callback: ((isBlock: Boolean) -> Unit)? = null

    @ColorInt
    private var confirmColor: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initFieldsFromBundle()
    }

    private fun initFieldsFromBundle() {
        arguments?.let {
            captionText = it.getString(ARG_DIALOG_TEXT) ?: ""
            confirmText = it.getString(ARG_CONFIRM_TEXT) ?: ""
            confirmColor = it.getInt(ARG_CONFIRM_COLOR)
            isBlock = it.getBoolean(ARG_BLOCK_BOX)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDeleteDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setButtonListeners()
        setDialogView()
    }

    private fun setDialogView() = with(binding) {
        if (isBlock) blockBox.show()
        dialogText.putText(captionText)
        confirmButton.putText(confirmText)
        confirmButton.setBackgroundColor(confirmColor)
        cancelButton.setOnClickListener { dismiss() }
        setWindowSize(this@DeleteFragmentDialog, WindowManager.LayoutParams.WRAP_CONTENT)
    }

    private fun setButtonListeners() = with(binding) {
        blockBtn.setOnClickListener {
            val color = if (blocked) MaterialColors.getColor(
                context,
                R.attr.strokeColor,
                "Should set color attribute first"
            )
            else ContextCompat.getColor(requireContext(), R.color.fillColor)
            animateBlockedStatus(it, color)
            blocked = !blocked
        }
        confirmButton.setOnClickListener {
            callback?.invoke(blocked)
            dismiss()
        }
    }

    private fun animateBlockedStatus(view: View, @ColorInt color: Int) {
        animator.animate(view) {
            (view as AppCompatImageView).setImageDrawable(
                ContextCompat.getDrawable(requireContext(), R.drawable.shotglass_empty)
            )
            view.drawable.setTint(color)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        callback = null
    }

    companion object {
        private const val ARG_DIALOG_TEXT = "DialogText"
        private const val ARG_CONFIRM_TEXT = "ConfirmText"
        private const val ARG_CONFIRM_COLOR = "ConfirmColor"
        private const val ARG_BLOCK_BOX = "BlockBox"

        @JvmStatic
        fun newInstance(
            dialogText: String,
            confirmText: String,
            @ColorInt confirmColor: Int,
            isBlock: Boolean = false,
            callback: ((isBlock: Boolean) -> Unit)
        ) = DeleteFragmentDialog().apply {
            arguments = bundleOf(
                ARG_DIALOG_TEXT to dialogText,
                ARG_CONFIRM_TEXT to confirmText,
                ARG_CONFIRM_COLOR to confirmColor,
                ARG_BLOCK_BOX to isBlock
            )
            this.callback = callback
        }
    }
}
package com.posse.kotlin1.calendar.view.deleteConfirmation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.annotation.ColorInt
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.posse.kotlin1.calendar.R
import com.posse.kotlin1.calendar.databinding.FragmentDeleteDialogBinding
import com.posse.kotlin1.calendar.utils.Animator
import com.posse.kotlin1.calendar.utils.putText
import com.posse.kotlin1.calendar.utils.setWindowSize
import com.posse.kotlin1.calendar.utils.show

const val ARG_DIALOG_TEXT = "DialogText"
const val ARG_CONFIRM_TEXT = "ConfirmText"
const val ARG_CONFIRM_COLOR = "ConfirmColor"
const val ARG_BLOCK_BOX = "BlockBox"

class DeleteFragmentDialog : DialogFragment() {
    private var _binding: FragmentDeleteDialogBinding? = null
    private val binding get() = _binding!!
    private lateinit var dialogText: String
    private lateinit var confirmText: String
    private val animator = Animator()
    private var isBlock = false
    private var blocked = false
    private var listener: DialogConfirmationListener? = null

    @ColorInt
    private var confirmColor: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            dialogText = it.getString(ARG_DIALOG_TEXT) ?: ""
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
        if (isBlock) binding.blockBox.show()
        binding.dialogText.putText(dialogText)
        binding.confirmButton.putText(confirmText)
        binding.confirmButton.setBackgroundColor(confirmColor)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.cancelButton.setOnClickListener { dismiss() }
        binding.blockBtn.setOnClickListener {
            if (blocked) {
                animator.animate(it) {
                    (it as AppCompatImageView).setImageDrawable(
                        ContextCompat.getDrawable(requireContext(), R.drawable.shotglass_empty)
                    )
                    it.drawable.setTint(
                        ContextCompat.getColor(requireContext(), R.color.strokeColor)
                    )
                }
            } else {
                animator.animate(it) {
                    (it as AppCompatImageView).setImageDrawable(
                        ContextCompat.getDrawable(requireContext(), R.drawable.shotglass_full)
                    )
                    it.drawable.setTint(
                        ContextCompat.getColor(requireContext(), R.color.fillColor)
                    )
                }
            }
            blocked = !blocked
        }
        binding.confirmButton.setOnClickListener {
            listener?.onConfirmClick(blocked)
            dismiss()
        }
        setWindowSize(this, WindowManager.LayoutParams.WRAP_CONTENT)
    }

    fun setListener(listener: DialogConfirmationListener) {
        this.listener = listener
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        listener = null
    }

    companion object {
        @JvmStatic
        fun newInstance(
            dialogText: String,
            confirmText: String,
            @ColorInt confirmColor: Int,
            isBlock: Boolean = false
        ) =
            DeleteFragmentDialog().apply {
                arguments = Bundle().apply {
                    putString(ARG_DIALOG_TEXT, dialogText)
                    putString(ARG_CONFIRM_TEXT, confirmText)
                    putInt(ARG_CONFIRM_COLOR, confirmColor)
                    putBoolean(ARG_BLOCK_BOX, isBlock)
                }
            }
    }
}

fun interface DialogConfirmationListener {
    fun onConfirmClick(isBlock: Boolean)
}
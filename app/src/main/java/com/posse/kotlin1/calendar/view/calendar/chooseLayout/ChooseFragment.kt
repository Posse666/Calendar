package com.posse.kotlin1.calendar.view.calendar.chooseLayout

import android.content.DialogInterface
import android.os.Bundle
import android.view.*
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import com.posse.kotlin1.calendar.databinding.DrinkPickerLayoutBinding
import com.posse.kotlin1.calendar.view.calendar.DrinkType

class ChooseFragment : DialogFragment() {

    private var _binding: DrinkPickerLayoutBinding? = null
    private val binding get() = _binding!!

    private var date: Int = 0
    private var callback: ((drinkType: DrinkType?) -> Unit)? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            date = it.getInt(DATE)
        }
        val params = WindowManager.LayoutParams().apply {
            gravity = Gravity.CENTER
            width = WindowManager.LayoutParams.WRAP_CONTENT
            height = WindowManager.LayoutParams.WRAP_CONTENT
        }
        dialog?.window?.attributes = params
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DrinkPickerLayoutBinding.inflate(inflater, container, false)
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setDateAtIcons()
        setClickListenersAtIcons()
    }

    private fun setClickListenersAtIcons() {
        binding.drinkPickerFullIcon.setOnClickListener {
            returnResult(DrinkType.Full)
            dismiss()
        }
        binding.drinkPickerHalfIcon.setOnClickListener {
            returnResult(DrinkType.Half)
            dismiss()
        }
        binding.drinkPickerEmptyIcon.setOnClickListener {
            returnResult(null)
            dismiss()
        }
    }

    private fun setDateAtIcons() {
        binding.drinkPickerEmptyIcon.setText(date.toString())
        binding.drinkPickerFullIcon.setText(date.toString())
        binding.drinkPickerHalfIcon.setText(date.toString())
    }

    private fun returnResult(drinkType: DrinkType?) = callback?.invoke(drinkType)

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        callback = null
    }

    companion object {
        fun newInstance(date: Int, callback: (drinkType: DrinkType?) -> Unit): ChooseFragment {
            return ChooseFragment().apply {
                arguments = bundleOf(DATE to date)
                this.callback = callback
            }
        }

        const val DATE = "Date"
    }
}
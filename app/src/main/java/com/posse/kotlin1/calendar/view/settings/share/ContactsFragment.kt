package com.posse.kotlin1.calendar.view.settings.share

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.posse.kotlin1.calendar.databinding.FragmentContactsBinding

private const val ARG_CONTACTS = "contacts"

class ContactsFragment : DialogFragment() {
    private var contacts: ArrayList<Contact>? = null
    private var _binding: FragmentContactsBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            contacts = it.getParcelableArrayList(ARG_CONTACTS)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentContactsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setWindowSize()
        isCancelable = true
        binding.btnSave.setOnClickListener {
            dismiss()
        }
        binding.btnCancel.setOnClickListener {
            dismiss()
        }
        addContacts()
    }

    private fun addContacts() {
        contacts?.forEach {
            binding.containerForContacts.add(requireContext(), it.name)
            binding.containerForContacts.add(requireContext(), it.email)
        }
    }

    private fun setWindowSize() {
        val dialogWindow = dialog?.window
        val params = dialogWindow?.attributes
        if (params != null) {
            params.width = WindowManager.LayoutParams.MATCH_PARENT
            params.height = WindowManager.LayoutParams.MATCH_PARENT
            dialogWindow.attributes = params
            dialogWindow.setBackgroundDrawableResource(android.R.color.transparent)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {

        @JvmStatic
        fun newInstance(contacts: ArrayList<Contact>) = ContactsFragment().apply {
            arguments = Bundle().apply {
                putParcelableArrayList(ARG_CONTACTS, contacts)
            }
        }
    }
}

private fun LinearLayout.add(context: Context, text: String) {
    val textView = TextView(context)
    textView.text = text
    addView(textView)
}
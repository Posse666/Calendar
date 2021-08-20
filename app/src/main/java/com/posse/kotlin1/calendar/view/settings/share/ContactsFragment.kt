package com.posse.kotlin1.calendar.view.settings.share

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.posse.kotlin1.calendar.R
import com.posse.kotlin1.calendar.databinding.FragmentContactsBinding
import com.posse.kotlin1.calendar.model.Contact
import com.posse.kotlin1.calendar.utils.Keyboard
import com.posse.kotlin1.calendar.utils.hide
import com.posse.kotlin1.calendar.utils.setWindowSize
import com.posse.kotlin1.calendar.utils.show
import com.posse.kotlin1.calendar.viewModel.ContactsViewModel

class ContactsFragment : DialogFragment(), ContactAdapterListener {
    private var _binding: FragmentContactsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ContactsViewModel by activityViewModels()
    private lateinit var adapter: ContactsListRecyclerAdapter
    private val keyboard = Keyboard()
    private val contacts: MutableSet<Contact> = mutableSetOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentContactsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setWindowSize(this, WindowManager.LayoutParams.MATCH_PARENT)
        binding.contactsListClose.setOnClickListener { dismiss() }
        binding.contactSearchField.editText?.doOnTextChanged { _, _, _, count ->
            if (count > 0) binding.contactSearchField.error = null
        }
        binding.contactAddButton.setOnClickListener {
            binding.contactSearchField.editText?.let { editText ->
                var isContactFound = false
                contacts.forEach {
                    if (it.email == editText.text.toString() && it.notInContacts) {
                        viewModel.contactClicked(it) {}
                        isContactFound = true
                        keyboard.hide(editText)
                        editText.text.clear()
                    }
                }
                if (!isContactFound) binding.contactSearchField.error =
                    getString(R.string.email_not_found)
            }
        }
        adapter = ContactsListRecyclerAdapter(mutableListOf(), this)
        binding.contactsRecyclerView.adapter = adapter
        viewModel.getLiveData().observe(viewLifecycleOwner, { pair ->
            if (pair.first) {
                if (pair.second.isEmpty()) {
                    binding.noContacts.show()
                    binding.contactsRecyclerCard.hide()
                } else {
                    contacts.clear()
                    contacts.addAll(pair.second)
                    val sortedContacts = pair.second.toSortedSet(compareBy(
                        { !it.notInContacts },
                        { !it.selected },
                        { it.names[0] }
                    ))
                    adapter.setData(
                        sortedContacts.toList().filter { !(it.notInContacts && !it.selected) })
                }
            }
        })
        isCancelable = true
    }

    override fun contactClicked(contact: Contact) {
        viewModel.contactClicked(contact) {
            Toast.makeText(
                requireContext(),
                "You are in black list of this user",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        keyboard.hide(binding.root)
        _binding = null
    }

    companion object {
        @JvmStatic
        fun newInstance() = ContactsFragment()
    }
}
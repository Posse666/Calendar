package com.posse.kotlin1.calendar.view.settings.share

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.DialogFragment
import com.posse.kotlin1.calendar.R
import com.posse.kotlin1.calendar.databinding.FragmentContactsBinding
import com.posse.kotlin1.calendar.model.Contact
import com.posse.kotlin1.calendar.utils.*
import com.posse.kotlin1.calendar.view.update.UpdateDialog
import com.posse.kotlin1.calendar.viewModel.ContactsViewModel

class ContactsFragment : DialogFragment(), ContactAdapterListener {

    private var _binding: FragmentContactsBinding? = null
    private val binding get() = _binding!!
    private var viewModel: ContactsViewModel? = null
    private var adapter: ContactsListRecyclerAdapter? = null
    private val keyboard = Keyboard()
    private val animator = Animator()
    private val contacts: MutableSet<Contact> = mutableSetOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentContactsBinding.inflate(inflater, container, false)
        .also { _binding = it }
        .root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setWindowSize(this, WindowManager.LayoutParams.MATCH_PARENT)
        binding.contactsListClose.setOnClickListener { dismiss() }
        setupContactSearchField()
        setupContactAddButton()
        setupRecyclerAdapter()
        viewModel?.getLiveData()?.observe(viewLifecycleOwner) { showContacts(it) }
        keyboard.setGlobalListener(activity?.window?.decorView?.rootView)
        keyboard.setListener { binding.contactSearchField.editText?.clearFocus() }
        isCancelable = true
    }

    private fun setupContactSearchField() = with(binding) {
        contactSearchField.editText?.doOnTextChanged { _, _, _, _ ->
            contactSearchField.error = null
        }
        contactSearchField.editText?.let {
            it.setOnEditorActionListener { textView, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_DONE) textView.clearFocus()
                false
            }
        }
        contactSearchField.setEndIconOnClickListener {
            contactSearchField.editText?.setText("")
            contactSearchField.error = null
        }
    }

    private fun setupRecyclerAdapter() {
        adapter = ContactsListRecyclerAdapter(mutableListOf(), this)
        binding.contactsRecyclerView.adapter = adapter
    }

    private fun showContacts(pair: Pair<Boolean, Set<Contact>>) {
        if (pair.first) {
            if (pair.second.isEmpty()) {
                binding.noContacts.show()
                binding.contactsRecyclerCard.hide()
            } else {
                contacts.clear()
                contacts.addAll(pair.second)
                val sortedContacts = pair.second.toSortedSet(compareBy(
                    { !it.notInContacts },
                    { it.notInBase },
                    { !it.selected },
                    { it.names[0] }
                ))
                adapter?.setData(
                    sortedContacts.toList().filter { !(it.notInContacts && !it.selected) })
                Handler(Looper.getMainLooper()).postDelayed({
                    binding.contactsRecyclerView.smoothScrollToPosition(0)
                }, 300)
            }
        }
    }

    private fun setupContactAddButton() {
        binding.contactAddButton.setOnClickListener { view ->
            animator.animate(view) {
                binding.contactSearchField.editText?.let { editText ->
                    val searchText = editText.text.toString().lowercase()
                    var isContactFound = false
                    contacts.forEach {
                        if (it.notInContacts) {
                            var matchFound = false
                            it.names.forEach { name ->
                                if (name.lowercase() == searchText) matchFound = true
                            }
                            if (it.email.lowercase() == searchText || matchFound) {
                                contactClicked(it)
                                isContactFound = true
                                keyboard.hide(editText)
                                editText.text.clear()
                            }
                        }
                    }
                    if (!isContactFound) binding.contactSearchField.error =
                        getString(R.string.contact_not_found)
                }
            }
        }
    }

    override fun contactClicked(contact: Contact) {
        viewModel?.contactClicked(contact) {
            when (it) {
                ContactsViewModel.ContactStatus.Blocked -> context?.showToast(getString(R.string.you_are_in_black_list))
                ContactsViewModel.ContactStatus.Offline -> context?.showToast(getString(R.string.no_connection))
                ContactsViewModel.ContactStatus.Error -> UpdateDialog.newInstance()
                    .show(childFragmentManager, null)
            }
        }
    }

    fun setViewModel(model: ContactsViewModel) {
        viewModel = model
    }

    override fun onDestroyView() {
        super.onDestroyView()
        keyboard.hide(binding.root)
        keyboard.setListener(null)
        keyboard.removeGlobalListener(activity?.window?.decorView?.rootView)
        _binding = null
        viewModel = null
    }

    companion object {
        @JvmStatic
        fun newInstance() = ContactsFragment()
    }
}
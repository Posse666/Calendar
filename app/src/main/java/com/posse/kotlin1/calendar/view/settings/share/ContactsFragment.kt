package com.posse.kotlin1.calendar.view.settings.share

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
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
        viewModel.getLiveData().observe(viewLifecycleOwner, { pair ->
            binding.contactsListClose.setOnClickListener { dismiss() }
            if (pair.first) {
                if (pair.second.isEmpty()) {
                    binding.noContacts.show()
                    binding.contactsRecyclerCard.hide()
                } else {
                    val sortedContacts = pair.second.toSortedSet(compareBy(
                        { it.notInContacts },
                        { it.isSelected },
                        { it.names[0] }
                    ))
                    adapter = ContactsListRecyclerAdapter(sortedContacts.toMutableList(),this)
                    binding.contactsRecyclerView.adapter = adapter
                }
            }
        })
        isCancelable = true
    }

    override fun contactClicked(contact: Contact) = viewModel.contactClicked(contact)

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
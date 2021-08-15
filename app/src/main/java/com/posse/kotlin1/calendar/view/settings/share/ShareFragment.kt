package com.posse.kotlin1.calendar.view.settings.share

import android.Manifest
import android.content.ContentResolver
import android.database.Cursor
import android.os.Bundle
import android.provider.ContactsContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.posse.kotlin1.calendar.R
import com.posse.kotlin1.calendar.databinding.FragmentShareBinding
import com.posse.kotlin1.calendar.model.Contact
import com.posse.kotlin1.calendar.utils.Permission
import com.posse.kotlin1.calendar.utils.checkPermission
import com.posse.kotlin1.calendar.utils.checkPermissionsResult
import com.posse.kotlin1.calendar.viewModel.ContactsViewModel

private const val REQUEST_CODE = 66

class ShareFragment : Fragment() {
    private var _binding: FragmentShareBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ContactsViewModel by activityViewModels()
    private val contactsWithEmail: MutableList<Contact> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentShareBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.shareButton.setOnClickListener {
            requirePermission()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray
    ) {
        when (requestCode) {
            REQUEST_CODE -> {
                when (checkPermissionsResult(
                    this,
                    grantResults,
                    getString(R.string.contact_access_description),
                    getString(R.string.contact_access_message),
                    getString(R.string.close)
                )) {
                    Permission.GRANTED -> getContacts()
                    Permission.NOT_GRANTED -> {
                    }
                }
            }
        }
    }

    private fun requirePermission() {
        when (checkPermission(
            REQUEST_CODE,
            this,
            Manifest.permission.READ_CONTACTS,
            getString(R.string.contact_access_description),
            getString(R.string.contact_access_message),
            getString(R.string.allow_access),
            getString(R.string.no_thanks)
        )) {
            Permission.GRANTED -> getContacts()
            Permission.NOT_GRANTED -> {
            }
        }
    }

    private fun getContacts() {
        contactsWithEmail.clear()
        context?.let {
            val contentResolver: ContentResolver = it.contentResolver
            val cursorWithContacts: Cursor? = contentResolver.query(
                ContactsContract.Contacts.CONTENT_URI,
                null,
                null,
                null,
                ContactsContract.Contacts.DISPLAY_NAME + " ASC"
            )

            cursorWithContacts?.let { cursor ->
                for (i in 0..cursor.count) {
                    if (cursor.moveToPosition(i)) {
                        val id =
                            cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID))
                        val cursorWithIDs: Cursor? = contentResolver.query(
                            ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?",
                            arrayOf(id),
                            null
                        )

                        cursorWithIDs?.let { cursor2 ->
                            for (j in 0..cursor2.count) {
                                if (cursor2.moveToPosition(j)) {
                                    val name =
                                        cursor2.getString(cursor2.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))
                                    val email =
                                        cursor2.getString(cursor2.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA))
                                    if (email != null) {
                                        var isNotAdded = true
                                        contactsWithEmail.forEach { contact ->
                                            if (contact.email == email) {
                                                if (!contact.names.contains(name)) contact.names[contact.names.size] = name
                                                isNotAdded = false
                                            }
                                        }
                                        if (isNotAdded) contactsWithEmail.add(Contact(arrayOf(name), email))
                                    }
                                }
                            }
                        }
                        cursorWithIDs?.close()
                    }
                }
            }
            cursorWithContacts?.close()
        }
        viewModel.setContacts(contactsWithEmail)
        ContactsFragment.newInstance().show(childFragmentManager, null)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {

        @JvmStatic
        fun newInstance() = ShareFragment()
    }
}
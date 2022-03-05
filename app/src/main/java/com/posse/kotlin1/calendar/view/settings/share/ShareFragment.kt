package com.posse.kotlin1.calendar.view.settings.share

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.pm.PackageManager
import android.database.Cursor
import android.os.Bundle
import android.provider.ContactsContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.posse.kotlin1.calendar.R
import com.posse.kotlin1.calendar.databinding.FragmentShareBinding
import com.posse.kotlin1.calendar.model.Contact
import com.posse.kotlin1.calendar.utils.Account
import com.posse.kotlin1.calendar.utils.showToast
import com.posse.kotlin1.calendar.view.update.UpdateDialog
import com.posse.kotlin1.calendar.viewModel.ContactsViewModel
import dagger.Lazy
import dagger.android.support.AndroidSupportInjection
import javax.inject.Inject

class ShareFragment : Fragment() {

    @Inject
    lateinit var account: Lazy<Account>

    @Inject
    lateinit var viewModelFactory: Lazy<ViewModelProvider.Factory>

    private var _binding: FragmentShareBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ContactsViewModel by lazy {
        viewModelFactory.get().create(ContactsViewModel::class.java)
    }

    private val requestPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) getContacts()
            else showAlertDialog()
        }

    private val contactsWithEmail: MutableList<Contact> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidSupportInjection.inject(this)
    }

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
            if (checkPermission()) getContacts()
        }
    }

    private fun checkPermission(): Boolean {
        val permission = Manifest.permission.READ_CONTACTS
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                permission
            ) == PackageManager.PERMISSION_GRANTED -> return true
            shouldShowRequestPermissionRationale(permission) -> {
                permission.showRequestDialog()
            }
            else -> requestPermission.launch(permission)
        }
        return false
    }

    private fun String.showRequestDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.contact_access_description))
            .setMessage(getString(R.string.contact_access_message))
            .setPositiveButton(getString(R.string.allow_access)) { _, _ ->
                requestPermission.launch(this)
            }
            .setNegativeButton(getString(R.string.no_thanks)) { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }

    private fun showAlertDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.contact_access_description))
            .setMessage(getString(R.string.contact_access_message))
            .setNegativeButton(getString(R.string.close)) { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }


    @SuppressLint("Range")
    private fun getContacts() {
        contactsWithEmail.clear()
        val contentResolver: ContentResolver = requireContext().contentResolver
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
                    fillContacts(cursorWithIDs)
                }
            }
        }
        cursorWithContacts?.close()

        val myMail = account.get().getEmail()
        if (myMail != null && myMail.contains("@")) {
            viewModel.setContacts(myMail, contactsWithEmail) {
                when (it) {
                    ContactsViewModel.ContactStatus.Blocked -> throw RuntimeException("Unexpected status: Blocked")
                    ContactsViewModel.ContactStatus.Offline -> context?.showToast(getString(R.string.no_connection))
                    ContactsViewModel.ContactStatus.Error -> UpdateDialog.newInstance()
                        .show(childFragmentManager, null)
                }
            }
            ContactsFragment
                .newInstance()
                .apply { setViewModel(viewModel) }
                .show(childFragmentManager, null)
        }
    }

    @SuppressLint("Range")
    private fun fillContacts(cursorWithIDs: Cursor?) {
        cursorWithIDs?.let { cursor ->
            for (j in 0..cursor.count) {
                if (cursor.moveToPosition(j)) {
                    val name =
                        cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))
                    val email =
                        cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA))
                    if (email != null) {
                        var isNotAdded = true
                        contactsWithEmail.forEach { contact ->
                            if (contact.email == email) {
                                if (!contact.names.contains(name))
                                    contact.names.add(name)
                                isNotAdded = false
                            }
                        }
                        if (isNotAdded) contactsWithEmail.add(Contact(mutableListOf(name), email))
                    }
                }
            }
        }
        cursorWithIDs?.close()
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
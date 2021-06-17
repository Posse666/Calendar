package com.posse.kotlin1.calendar.view.settings.share

import android.Manifest
import android.content.ContentResolver
import android.content.pm.PackageManager
import android.database.Cursor
import android.os.Bundle
import android.provider.ContactsContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.posse.kotlin1.calendar.R
import com.posse.kotlin1.calendar.databinding.FragmentShareBinding

const val REQUEST_CODE = 66

class ShareFragment : Fragment() {

    private var _binding: FragmentShareBinding? = null
    private val binding get() = _binding!!
    private val contactsWithEmail: ArrayList<Contact> = arrayListOf()

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
            checkPermission()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    ) {
        when (requestCode) {
            REQUEST_CODE -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                ) {
                    getContacts()
                } else {
                    context?.let {
                        AlertDialog.Builder(it)
                            .setTitle(getString(R.string.contact_access_description))
                            .setMessage(getString(R.string.contact_access_message))
                            .setNegativeButton(getString(R.string.close)) { dialog, _ ->
                                dialog.dismiss()
                            }
                            .create()
                            .show()
                    }
                }
                return
            }
        }
    }

    private fun checkPermission() {
        context?.let {
            when {
                ContextCompat.checkSelfPermission(it, Manifest.permission.READ_CONTACTS) ==
                        PackageManager.PERMISSION_GRANTED -> {
                    getContacts()
                }
                shouldShowRequestPermissionRationale(Manifest.permission.READ_CONTACTS) -> {
                    AlertDialog.Builder(it)
                        .setTitle(getString(R.string.contact_access_description))
                        .setMessage(getString(R.string.contact_access_message))
                        .setPositiveButton(getString(R.string.allow_access)) { _, _ ->
                            requestPermission()
                        }
                        .setNegativeButton(getString(R.string.no_thanks)) { dialog, _ ->
                            dialog.dismiss()
                        }
                        .create()
                        .show()
                }
                else -> {
                    requestPermission()
                }
            }
        }
    }

    private fun requestPermission() {
        requestPermissions(arrayOf(Manifest.permission.READ_CONTACTS), REQUEST_CODE)
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
                                    if (email != null) contactsWithEmail.add(Contact(name, email))
                                }
                            }
                        }
                        cursorWithIDs?.close()
                    }
                }
            }
            cursorWithContacts?.close()
        }
        ContactsFragment.newInstance(contactsWithEmail)
            .show(requireActivity().supportFragmentManager, null)
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
package com.posse.kotlin1.calendar.view.settings.share

import androidx.recyclerview.widget.RecyclerView
import com.posse.kotlin1.calendar.databinding.ContactLayoutBinding
import com.posse.kotlin1.calendar.model.Contact
import com.posse.kotlin1.calendar.utils.disappear
import com.posse.kotlin1.calendar.utils.putText

class ContactViewHolder(
    val contactBinding: ContactLayoutBinding,
    private val listener: ContactClickListener
) :
    RecyclerView.ViewHolder(contactBinding.root) {

    fun bind(contact: Contact) {
        var name: String = contact.names[0]
        if (contact.names.size > 1) {
            for (i in 1..contact.names.size) name += "\n${contact.names[i]}"
        }
        contactBinding.contactName.putText(name)
        contactBinding.contactEmail.putText(contact.email)
        setupCheckedView(contact)
        setupContactClickable(contact)
    }

    private fun setupContactClickable(contact: Contact) {
        if (!contact.notInBase) contactBinding.contactCardView.setOnClickListener {
            listener.selectItem(contact)
        }
    }

    private fun setupCheckedView(contact: Contact) {
        if (contact.notInBase) {
            contactBinding.contactCardView.alpha = 0.6f
            contactBinding.contactChecked.disappear()
        } else contactBinding.contactChecked.setImageResource(
            if (contact.selected) android.R.drawable.radiobutton_on_background
            else android.R.drawable.radiobutton_off_background
        )
    }
}

interface ContactClickListener {
    fun selectItem(contact: Contact)
}
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
            contact.names.forEach {
                name += "\n$it"
            }
        }
        contactBinding.contactName.putText(name)
        contactBinding.contactEmail.putText(contact.email)
        setupCheckedView(contact)
        contactBinding.contactCardView.setOnClickListener {
            listener.selectItem(contact)
        }
    }

    private fun setupCheckedView(contact: Contact) {
        if (contact.notInBase) {
            contactBinding.contactCardView.alpha = 0.7f
            contactBinding.contactChecked.disappear()
        } else contactBinding.contactChecked.setImageResource(
            if (contact.isSelected) android.R.drawable.radiobutton_on_background
            else android.R.drawable.radiobutton_off_background
        )
    }
}

interface ContactClickListener {
    fun selectItem(contact: Contact)
}
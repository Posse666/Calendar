package com.posse.kotlin1.calendar.view.settings.share

import androidx.recyclerview.widget.RecyclerView
import com.posse.kotlin1.calendar.R
import com.posse.kotlin1.calendar.databinding.ContactLayoutBinding
import com.posse.kotlin1.calendar.model.Contact
import com.posse.kotlin1.calendar.utils.disappear
import com.posse.kotlin1.calendar.utils.putText

class ContactViewHolder(
    val contactBinding: ContactLayoutBinding,
    private val listener: ContactClickListener
) : RecyclerView.ViewHolder(contactBinding.root) {

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

    private fun setupContactClickable(contact: Contact) =
        if (!contact.notInBase) contactBinding.contactCardView.setOnClickListener {
            listener.selectItem(contact)
        } else Unit

    private fun setupCheckedView(contact: Contact) = if (contact.notInBase) {
        contactBinding.contactCardView.alpha = 0.6f
        contactBinding.contactChecked.disappear()
    } else {
        contactBinding.contactChecked.setImageResource(
            if (contact.selected) R.drawable.shotglass_full
            else R.drawable.shotglass_empty
        )
        if (contact.selected) contactBinding.contactChecked.drawable.setTint(
            contactBinding.root.context.getColor(R.color.fillColor)
        ) else Unit
    }
}

interface ContactClickListener {
    fun selectItem(contact: Contact)
}
package com.posse.kotlin1.calendar.view.settings.share

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.posse.kotlin1.calendar.databinding.ContactLayoutBinding
import com.posse.kotlin1.calendar.model.Contact
import com.posse.kotlin1.calendar.utils.Change
import com.posse.kotlin1.calendar.utils.DiffUtilCallback
import com.posse.kotlin1.calendar.utils.createCombinedPayload

class ContactsListRecyclerAdapter(
    private var data: MutableList<Contact>,
    private val listener: ContactAdapterListener
) : RecyclerView.Adapter<ContactViewHolder>(), ContactClickListener {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val contactBinding = ContactLayoutBinding.inflate(inflater, parent, false)
        return ContactViewHolder(contactBinding, this)
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        holder.bind(data[position])
    }

    override fun onBindViewHolder(
        holder: ContactViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        if (payloads.isEmpty())
            super.onBindViewHolder(holder, position, payloads)
        else {
            val combinedChange =
                createCombinedPayload(payloads as List<Change<Contact>>)
            val oldData = combinedChange.oldData
            val newData = combinedChange.newData

            if (newData.selected != oldData.selected) {
                holder.contactBinding.contactChecked.setImageResource(
                    if (newData.selected) android.R.drawable.radiobutton_on_background
                    else android.R.drawable.radiobutton_off_background
                )
            }
        }
    }

    fun setData(newItems: List<Contact>) {
        val result = DiffUtil.calculateDiff(DiffUtilCallback(data, newItems))
        result.dispatchUpdatesTo(this)
        data.clear()
        data.addAll(newItems)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun selectItem(contact: Contact) {
        listener.contactClicked(contact)
    }
}

interface ContactAdapterListener {
    fun contactClicked(contact: Contact)
}
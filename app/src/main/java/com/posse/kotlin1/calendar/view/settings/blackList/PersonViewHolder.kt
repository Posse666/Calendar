package com.posse.kotlin1.calendar.view.settings.blackList

import androidx.recyclerview.widget.RecyclerView
import com.posse.kotlin1.calendar.databinding.PersonLayoutBinding
import com.posse.kotlin1.calendar.model.Friend
import com.posse.kotlin1.calendar.utils.putText

class PersonViewHolder(
    private val friendBinding: PersonLayoutBinding,
    private val listener: PersonClickListener
) : RecyclerView.ViewHolder(friendBinding.root) {

    fun bind(friend: Friend) {
        friendBinding.nameField.putText(friend.name)
        friendBinding.personEmail.putText(friend.email)
        friendBinding.removeBlackList.setOnClickListener {
            listener.clickItem(friend)
        }
    }
}

fun interface PersonClickListener {
    fun clickItem(friend: Friend)
}
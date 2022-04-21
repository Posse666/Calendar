package com.posse.kotlin1.calendar.view.friends.list

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.MotionEvent
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.RecyclerView
import com.posse.kotlin1.calendar.R
import com.posse.kotlin1.calendar.databinding.FriendLayoutBinding
import com.posse.kotlin1.calendar.common.data.model.Friend
import com.posse.kotlin1.calendar.utils.Keyboard
import com.posse.kotlin1.calendar.utils.putText

class FriendViewHolder(
    private val friendBinding: FriendLayoutBinding,
    private val dragListener: OnStartDragListener,
    private val listener: FriendAdapterListener,
    private val keyboard: Keyboard
) : RecyclerView.ViewHolder(friendBinding.root), ItemTouchHelperViewHolder {

    fun bind(friend: Friend) {
        keyboard.setListener { friendBinding.editNameField.editText?.clearFocus() }
        setupViewText(friend)
        setupCheckedView(friend)
        setupDragListener()
        setupCardClickListener(friend)
        setupDeleteButton(friend)
    }

    private fun setupDeleteButton(friend: Friend) {
        friendBinding.deleteFriend.setOnClickListener {
            listener.friendDeleted(friend)
        }
    }

    private fun setupViewText(friend: Friend) {
        friendBinding.editNameField.editText?.setText(friend.name)
        friendBinding.editNameField.editText?.doOnTextChanged { text, _, _, _ ->
            friend.name = text.toString()
            listener.friendNameChanged(friend)
        }
        friendBinding.friendEmail.putText(friend.email)
    }

    private fun setupCheckedView(friend: Friend) {
        friendBinding.friendChecked.setImageResource(
            if (friend.selected) R.drawable.shotglass_full
            else R.drawable.shotglass_empty
        )
        if (friend.selected) friendBinding.friendChecked.setColorFilter(
            friendBinding.root.context.getColor(R.color.fillColor)
        )
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupDragListener() {
        friendBinding.dragHandleFriend.setOnTouchListener { _, event ->
            if (event.actionMasked == MotionEvent.ACTION_DOWN) dragListener.onStartDrag(this)
            false
        }
    }

    private fun setupCardClickListener(friend: Friend) {
        friendBinding.friendCardView.setOnClickListener {
            keyboard.hide(it)
            friend.selected = true
            listener.friendClicked(friend)
        }
    }

    fun removeListeners() = keyboard.setListener(null)

    override fun onItemSelected() = friendBinding.friendCardView.background.setTint(Color.LTGRAY)

    override fun onItemClear() = friendBinding.friendCardView.background.setTintList(null)
}

interface ItemTouchHelperViewHolder {
    fun onItemSelected()
    fun onItemClear()
}
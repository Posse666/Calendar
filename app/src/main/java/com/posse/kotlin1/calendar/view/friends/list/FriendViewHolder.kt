package com.posse.kotlin1.calendar.view.friends.list

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.MotionEvent
import androidx.recyclerview.widget.RecyclerView
import com.posse.kotlin1.calendar.databinding.FriendLayoutBinding
import com.posse.kotlin1.calendar.model.Friend

class FriendViewHolder(
    private val friendBinding: FriendLayoutBinding,
    private val dragListener: OnStartDragListener
) :
    RecyclerView.ViewHolder(friendBinding.root), ItemTouchHelperViewHolder {

    @SuppressLint("ClickableViewAccessibility")
    fun bind(friend: Friend) {
        friendBinding.editNameField.setText(friend.name)
        friendBinding.friendEmail.text = friend.email
        friendBinding.friendChecked.setImageResource(
            if (friend.isSelected) android.R.drawable.radiobutton_on_background
            else android.R.drawable.radiobutton_off_background
        )
        friendBinding.dragHandleFriend.setOnTouchListener { _, event ->
            if (event.actionMasked == MotionEvent.ACTION_DOWN) {
                dragListener.onStartDrag(this)
            }
            false
        }
    }

    override fun onItemSelected() {
        friendBinding.friendCardView.setCardBackgroundColor(Color.LTGRAY)
    }

    override fun onItemClear() {
        friendBinding.friendCardView.setCardBackgroundColor(Color.WHITE)
    }
}

interface ItemTouchHelperViewHolder {
    fun onItemSelected()
    fun onItemClear()
}
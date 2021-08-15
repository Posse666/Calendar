package com.posse.kotlin1.calendar.view.friends.list

import android.annotation.SuppressLint
import android.graphics.Color
import android.text.method.KeyListener
import android.text.method.MovementMethod
import android.view.MotionEvent
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.posse.kotlin1.calendar.databinding.FriendLayoutBinding
import com.posse.kotlin1.calendar.model.Friend
import com.posse.kotlin1.calendar.utils.*

class FriendViewHolder(
    val friendBinding: FriendLayoutBinding,
    private val dragListener: OnStartDragListener,
    private val listener: ItemClickListener
) :
    RecyclerView.ViewHolder(friendBinding.root), ItemTouchHelperViewHolder {
    private val keyboard = Keyboard()
    private val movementMethod = friendBinding.editNameField.movementMethod
    private val keyListener = friendBinding.editNameField.keyListener

    fun bind(friend: Friend) {
        setTouchResponse(null, null)
        switchElements(friendBinding.saveFriend, friendBinding.editFriend)
        setupViewText(friend)
        setupCheckedView(friend)
        setupDragListener()
        setupEditButton()
        setupSaveButton(friend)
        setupCardClickListener(friend)
        setupDeleteButton(friend)
    }

    private fun setupDeleteButton(friend: Friend) {
        friendBinding.deleteFriend.setOnClickListener {
            listener.deleteItem(friend)
        }
    }

    private fun setupViewText(friend: Friend) {
        friendBinding.editNameField.putText(friend.name)
        friendBinding.friendEmail.putText(friend.email)
    }

    private fun setupCheckedView(friend: Friend) {
        friendBinding.friendChecked.setImageResource(
            if (friend.isSelected) android.R.drawable.radiobutton_on_background
            else android.R.drawable.radiobutton_off_background
        )
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupDragListener() {
        friendBinding.dragHandleFriend.setOnTouchListener { _, event ->
            if (event.actionMasked == MotionEvent.ACTION_DOWN) {
                dragListener.onStartDrag(this)
            }
            false
        }
    }

    private fun setupCardClickListener(friend: Friend) {
        friendBinding.friendCardView.setOnClickListener {
            keyboard.hide(it)
            friend.isSelected = true
            listener.saveItem(friend)
        }
    }

    private fun setupSaveButton(friend: Friend) {
        friendBinding.saveFriend.setOnClickListener {
            switchElements(it, friendBinding.editFriend)
            friendBinding.editNameField.removeFocus()
            setTouchResponse(null, null)
            keyboard.hide(it)
            friend.name = friendBinding.editNameField.text.toString()
            listener.saveItem(friend)
        }
    }

    private fun setupEditButton() {
        friendBinding.editFriend.setOnClickListener {
            switchElements(it, friendBinding.saveFriend)
            friendBinding.editNameField.setFocus()
            friendBinding.editNameField.setSelection(friendBinding.editNameField.text.toString().length)
            setTouchResponse(movementMethod, keyListener)
            keyboard.show()
        }
    }

    private fun setTouchResponse(movementMethod: MovementMethod?, keyListener: KeyListener?) {
        friendBinding.editNameField.movementMethod = movementMethod
        friendBinding.editNameField.keyListener = keyListener
    }

    private fun switchElements(view: View, view2: View) {
        view.hide()
        view2.show()
    }

    override fun onItemSelected() {
        friendBinding.friendCardView.background.setTint(Color.LTGRAY)
    }

    override fun onItemClear() {
        friendBinding.friendCardView.background.setTintList(null)
    }
}

interface ItemTouchHelperViewHolder {
    fun onItemSelected()
    fun onItemClear()
}

interface ItemClickListener {
    fun saveItem(friend: Friend)
    fun deleteItem(friend: Friend)
}
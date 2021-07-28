package com.posse.kotlin1.calendar.view.friends.list

import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.Color
import android.graphics.Rect
import android.text.method.KeyListener
import android.text.method.MovementMethod
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.recyclerview.widget.RecyclerView
import com.posse.kotlin1.calendar.databinding.FriendLayoutBinding
import com.posse.kotlin1.calendar.model.Friend
import com.posse.kotlin1.calendar.utils.*

class FriendViewHolder(
    val friendBinding: FriendLayoutBinding,
    private val dragListener: OnStartDragListener,
    private val activity: Activity,
    private val listener: ItemClickListener
) :
    RecyclerView.ViewHolder(friendBinding.root), ItemTouchHelperViewHolder {

    private val movementMethod = friendBinding.editNameField.movementMethod
    private val keyListener = friendBinding.editNameField.keyListener
    private var isKeyboardOpened = false
    private val inputMethodManager =
        activity.getSystemService(android.content.Context.INPUT_METHOD_SERVICE) as InputMethodManager

    @SuppressLint("ClickableViewAccessibility")
    fun bind(friend: Friend) {
        setTouchResponse(null, null)
        switchElements(friendBinding.saveFriend, friendBinding.editFriend)

        friendBinding.editNameField.putText(friend.name)
        friendBinding.friendEmail.putText(friend.email)

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

        friendBinding.editFriend.setOnClickListener {
            switchElements(it, friendBinding.saveFriend)
            friendBinding.editNameField.setFocus()
            friendBinding.editNameField.setSelection(friendBinding.editNameField.text.toString().length)
            setTouchResponse(movementMethod, keyListener)
            showKeyboard()
        }

        friendBinding.saveFriend.setOnClickListener {
            switchElements(it, friendBinding.editFriend)
            friendBinding.editNameField.removeFocus()
            setTouchResponse(null, null)
            hideKeyboard(it)
        }

        friendBinding.friendCardView.setOnClickListener {
            hideKeyboard(it)
            listener.onItemClicked(friend)
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

    private fun showKeyboard() {
        val rootView: View = activity.window.decorView.rootView
        rootView.viewTreeObserver.addOnGlobalLayoutListener {
            val r = Rect()
            rootView.getWindowVisibleDisplayFrame(r)
            val screenHeight = rootView.height
            val keypadHeight = screenHeight - r.bottom
            isKeyboardOpened = keypadHeight > screenHeight * 0.2
        }
        if (!isKeyboardOpened) {
            inputMethodManager.toggleSoftInput(
                InputMethodManager.SHOW_FORCED,
                0
            )
        }
    }

    private fun hideKeyboard(view: View) {
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
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

interface ItemClickListener {
    fun onItemClicked(friend: Friend)
}
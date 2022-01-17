package com.posse.kotlin1.calendar.view.friends.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.posse.kotlin1.calendar.databinding.FriendLayoutBinding
import com.posse.kotlin1.calendar.model.Friend
import com.posse.kotlin1.calendar.utils.DiffUtilCallback
import com.posse.kotlin1.calendar.utils.Keyboard

class FriendListRecyclerAdapter(
    private var data: MutableList<Friend>,
    private val listener: FriendAdapterListener,
    private val keyboard: Keyboard,
    private val dragListener: OnStartDragListener
) : RecyclerView.Adapter<FriendViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val friendBinding = FriendLayoutBinding.inflate(inflater, parent, false)
        return FriendViewHolder(friendBinding, dragListener, listener, keyboard)
    }

    override fun onBindViewHolder(holder: FriendViewHolder, position: Int) {
        data[position].position = position
        holder.bind(data[position])
    }

    override fun getItemCount(): Int {
        return data.size
    }

    fun onItemMove(fromPosition: Int, toPosition: Int) {
        data.removeAt(fromPosition).apply {
            val position = if (toPosition > fromPosition) toPosition - 1 else toPosition
            data.add(position, this)
            listener.friendMoved(fromPosition, toPosition)
        }
        notifyItemMoved(fromPosition, toPosition)
    }

    fun setData(newItems: MutableList<Friend>) {
        val result = DiffUtil.calculateDiff(DiffUtilCallback(data, newItems))
        result.dispatchUpdatesTo(this)
        data.clear()
        data.addAll(newItems)
    }

    override fun onViewRecycled(holder: FriendViewHolder) {
        holder.removeListeners()
        super.onViewRecycled(holder)
    }
}

fun interface OnStartDragListener {
    fun onStartDrag(viewHolder: RecyclerView.ViewHolder)
}

interface FriendAdapterListener {
    fun friendClicked(friend: Friend): Unit?
    fun friendMoved(fromPosition: Int, toPosition: Int): Unit?
    fun friendDeleted(friend: Friend)
    fun friendNameChanged(friend: Friend): Unit?
}
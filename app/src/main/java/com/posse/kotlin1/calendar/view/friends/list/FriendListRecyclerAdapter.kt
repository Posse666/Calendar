package com.posse.kotlin1.calendar.view.friends.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.posse.kotlin1.calendar.databinding.FriendLayoutBinding
import com.posse.kotlin1.calendar.model.Friend

class FriendListRecyclerAdapter(
    private var data: MutableList<Friend>,
    private val dragListener: OnStartDragListener,
    private val listener: FriendAdapterListener
) : RecyclerView.Adapter<FriendViewHolder>(), ItemClickListener {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val friendBinding = FriendLayoutBinding.inflate(inflater, parent, false)
        return FriendViewHolder(friendBinding, dragListener, this)
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

    override fun saveItem(friend: Friend) = listener.friendClicked(friend)

    override fun deleteItem(friend: Friend) = listener.friendDeleted(friend)
}

fun interface OnStartDragListener {
    fun onStartDrag(viewHolder: RecyclerView.ViewHolder)
}

interface FriendAdapterListener {
    fun friendClicked(friend: Friend)
    fun friendMoved(fromPosition: Int, toPosition: Int)
    fun friendDeleted(friend: Friend)
}
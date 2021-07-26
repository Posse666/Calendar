package com.posse.kotlin1.calendar.view.friends.list

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.posse.kotlin1.calendar.databinding.FriendLayoutBinding
import com.posse.kotlin1.calendar.model.Friend

class FriendListRecyclerAdapter(
    private var data: MutableList<Friend>,
    private val dragListener: OnStartDragListener,
    private val activity: Activity
) : RecyclerView.Adapter<FriendViewHolder>(), ItemTouchHelperAdapter, ItemClickListener {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val friendBinding = FriendLayoutBinding.inflate(inflater, parent, false)
        return FriendViewHolder(friendBinding, dragListener, activity, this)
    }

    override fun onBindViewHolder(holder: FriendViewHolder, position: Int) {
        holder.bind(data[position])
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onItemMove(fromPosition: Int, toPosition: Int) {
        data.removeAt(fromPosition).apply {
            data.add(if (toPosition > fromPosition) toPosition - 1 else toPosition, this)
        }
        notifyItemMoved(fromPosition, toPosition)
    }

    fun setData(friends: MutableList<Friend>) {
        data.clear()
        data.addAll(friends)
        notifyDataSetChanged()
    }

    override fun onItemClicked(friend: Friend) {
        data.forEach {
            it.isSelected = false
        }
        friend.isSelected = true
        notifyDataSetChanged()
    }
}

interface ItemTouchHelperAdapter {
    fun onItemMove(fromPosition: Int, toPosition: Int)
}

interface OnStartDragListener {
    fun onStartDrag(viewHolder: RecyclerView.ViewHolder)
}
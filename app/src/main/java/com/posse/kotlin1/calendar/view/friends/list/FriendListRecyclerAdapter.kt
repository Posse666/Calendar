package com.posse.kotlin1.calendar.view.friends.list

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.posse.kotlin1.calendar.databinding.FriendLayoutBinding
import com.posse.kotlin1.calendar.model.Friend
import com.posse.kotlin1.calendar.utils.Change
import com.posse.kotlin1.calendar.utils.DiffUtilCallback
import com.posse.kotlin1.calendar.utils.createCombinedPayload

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
        data[position].position = position
        holder.bind(data[position])
    }

    override fun onBindViewHolder(
        holder: FriendViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        if (payloads.isEmpty())
            super.onBindViewHolder(holder, position, payloads)
        else {
            val combinedChange =
                createCombinedPayload(payloads as List<Change<Friend>>)
            val oldData = combinedChange.oldData
            val newData = combinedChange.newData

            if (newData.isSelected != oldData.isSelected) {
                holder.friendBinding.friendChecked.setImageResource(
                    if (newData.isSelected) android.R.drawable.radiobutton_on_background
                    else android.R.drawable.radiobutton_off_background
                )
            }
        }
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

    fun setData(newItems: MutableList<Friend>) {
        val result = DiffUtil.calculateDiff(DiffUtilCallback(data, newItems))
        result.dispatchUpdatesTo(this)
        data.clear()
        data.addAll(newItems)
    }

    override fun onItemClicked(friend: Friend) {
        val newData = data.map { it.copy() }
        newData.forEach {
            if (it.id == friend.id) it.isSelected = true
            else if (it.isSelected) it.isSelected = false
        }
        setData(newData.toMutableList())
    }
}

interface ItemTouchHelperAdapter {
    fun onItemMove(fromPosition: Int, toPosition: Int)
}

interface OnStartDragListener {
    fun onStartDrag(viewHolder: RecyclerView.ViewHolder)
}
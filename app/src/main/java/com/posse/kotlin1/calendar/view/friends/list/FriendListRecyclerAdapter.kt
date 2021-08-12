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
    private val activity: Activity,
    private val listener: FriendAdapterListener
) : RecyclerView.Adapter<FriendViewHolder>(), ItemClickListener {

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
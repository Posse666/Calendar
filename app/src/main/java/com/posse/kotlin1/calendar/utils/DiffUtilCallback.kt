package com.posse.kotlin1.calendar.utils

import androidx.recyclerview.widget.DiffUtil
import com.posse.kotlin1.calendar.model.Friend

class DiffUtilCallback(
    private var oldItems: List<Friend>,
    private var newItems: List<Friend>
) : DiffUtil.Callback() {

    override fun getOldListSize(): Int = oldItems.size

    override fun getNewListSize(): Int = newItems.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
        oldItems[oldItemPosition].email == newItems[newItemPosition].email

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
        oldItems[oldItemPosition].isSelected == newItems[newItemPosition].isSelected

    override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any {
        val oldItem = oldItems[oldItemPosition]
        val newItem = newItems[newItemPosition]

        return Change(
            oldItem,
            newItem
        )
    }
}
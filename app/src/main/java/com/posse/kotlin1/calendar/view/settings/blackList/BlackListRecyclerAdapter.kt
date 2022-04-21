package com.posse.kotlin1.calendar.view.settings.blackList

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.posse.kotlin1.calendar.databinding.PersonLayoutBinding
import com.posse.kotlin1.calendar.common.data.model.Friend
import com.posse.kotlin1.calendar.utils.DiffUtilCallback

class BlackListRecyclerAdapter(
    private var data: MutableList<Friend>,
    private val listener: PersonClickListener
) : RecyclerView.Adapter<PersonViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PersonViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val personBinding = PersonLayoutBinding.inflate(inflater, parent, false)
        return PersonViewHolder(personBinding, listener)
    }

    override fun onBindViewHolder(holder: PersonViewHolder, position: Int) =
        holder.bind(data[position])

    override fun getItemCount(): Int = data.size

    fun setData(newItems: MutableList<Friend>) {
        val result = DiffUtil.calculateDiff(DiffUtilCallback(data, newItems))
        result.dispatchUpdatesTo(this)
        data.clear()
        data.addAll(newItems)
    }
}
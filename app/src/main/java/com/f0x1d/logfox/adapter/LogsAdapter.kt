package com.f0x1d.logfox.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.f0x1d.logfox.adapter.base.BaseAdapter
import com.f0x1d.logfox.databinding.ItemLogBinding
import com.f0x1d.logfox.model.LogLine
import com.f0x1d.logfox.ui.viewholder.LogViewHolder

class LogsAdapter: BaseAdapter<LogLine, ItemLogBinding>() {

    val expandedStates = mutableMapOf<Long, Boolean>()
    val selectedItems = mutableListOf<LogLine>()

    override fun createHolder(layoutInflater: LayoutInflater, parent: ViewGroup) = LogViewHolder(
        ItemLogBinding.inflate(layoutInflater, parent, false)
    )

    override fun getItemId(position: Int) = elements[position].id

    fun clearSelected() {
        selectedItems.clear()
        notifyItemRangeChanged(0, itemCount)
    }
}
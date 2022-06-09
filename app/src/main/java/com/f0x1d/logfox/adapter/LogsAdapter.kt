package com.f0x1d.logfox.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.f0x1d.logfox.adapter.base.BaseAdapter
import com.f0x1d.logfox.databinding.ItemTextBinding
import com.f0x1d.logfox.logging.model.LogLine
import com.f0x1d.logfox.viewholder.LogViewHolder

class LogsAdapter(private val longClick: (LogLine) -> Unit): BaseAdapter<LogLine, ItemTextBinding>() {

    val expandedStates = mutableMapOf<Long, Boolean>()

    override fun createHolder(layoutInflater: LayoutInflater, parent: ViewGroup) = LogViewHolder(
        ItemTextBinding.inflate(layoutInflater, parent, false),
        longClick
    )

    override fun getItemId(position: Int) = elements[position].id
}
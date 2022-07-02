package com.f0x1d.logfox.utils

import androidx.recyclerview.widget.DiffUtil
import com.f0x1d.logfox.model.LogLine

class LogLinesDiffUtilCallback(private val oldList: List<LogLine>, private val newList: List<LogLine>): DiffUtil.Callback() {
    override fun getOldListSize() = oldList.size
    override fun getNewListSize() = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) = oldList[oldItemPosition].id == newList[newItemPosition].id
    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int) = oldList[oldItemPosition] == newList[newItemPosition]
}
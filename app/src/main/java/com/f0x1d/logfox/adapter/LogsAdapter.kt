package com.f0x1d.logfox.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import com.f0x1d.logfox.adapter.base.BaseListAdapter
import com.f0x1d.logfox.databinding.ItemLogBinding
import com.f0x1d.logfox.model.LogLine
import com.f0x1d.logfox.ui.viewholder.LogViewHolder
import com.f0x1d.logfox.utils.preferences.AppPreferences

class LogsAdapter(
    private val appPreferences: AppPreferences,
    val levelColorCacheMap: MutableMap<Int, Int>,
    private val copyLog: (LogLine) -> Unit
): BaseListAdapter<LogLine, ItemLogBinding>(LOGLINE_DIFF) {

    companion object {
        private val LOGLINE_DIFF = object : DiffUtil.ItemCallback<LogLine>() {
            override fun areItemsTheSame(oldItem: LogLine, newItem: LogLine) = oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: LogLine, newItem: LogLine) = oldItem == newItem
        }
    }

    val expandedStates = mutableMapOf<Long, Boolean>()
    val selectedItems = mutableListOf<LogLine>()

    var textSize = appPreferences.logsTextSize.toFloat()
        set(value) {
            field = value
            notifyItemRangeChanged(0, itemCount)
        }
    var logsExpanded = appPreferences.logsExpanded
        set(value) {
            field = value
            notifyItemRangeChanged(0, itemCount)
        }
    var logsFormat = appPreferences.showLogValues
        set(value) {
            field = value
            notifyItemRangeChanged(0, itemCount)
        }

    override fun createHolder(layoutInflater: LayoutInflater, parent: ViewGroup) = LogViewHolder(
        binding = ItemLogBinding.inflate(layoutInflater, parent, false),
        copyLog = copyLog
    )

    fun clearSelected() {
        selectedItems.clear()
        notifyItemRangeChanged(0, itemCount)
    }
}
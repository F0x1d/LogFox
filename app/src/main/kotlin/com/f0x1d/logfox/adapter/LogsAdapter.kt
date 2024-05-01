package com.f0x1d.logfox.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import com.f0x1d.logfox.adapter.base.BaseListAdapter
import com.f0x1d.logfox.databinding.ItemLogBinding
import com.f0x1d.logfox.ui.viewholder.LogViewHolder
import com.f0x1d.logfox.utils.preferences.AppPreferences

class LogsAdapter(
    private val appPreferences: AppPreferences,
    private val selectedItem: (com.f0x1d.logfox.model.LogLine, Boolean) -> Unit,
    private val copyLog: (com.f0x1d.logfox.model.LogLine) -> Unit
): BaseListAdapter<com.f0x1d.logfox.model.LogLine, ItemLogBinding>(LOGLINE_DIFF) {

    companion object {
        private val LOGLINE_DIFF = object : DiffUtil.ItemCallback<com.f0x1d.logfox.model.LogLine>() {
            override fun areItemsTheSame(oldItem: com.f0x1d.logfox.model.LogLine, newItem: com.f0x1d.logfox.model.LogLine) = oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: com.f0x1d.logfox.model.LogLine, newItem: com.f0x1d.logfox.model.LogLine) = oldItem == newItem
        }
    }

    val expandedStates = mutableMapOf<Long, Boolean>()
    var selectedItems = emptyList<com.f0x1d.logfox.model.LogLine>()
        set(value) {
            field = value
            notifyItemRangeChanged(0, itemCount)
        }

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
        selectedItem = selectedItem,
        copyLog = copyLog
    )
}

package com.f0x1d.feature.logging.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.f0x1d.feature.logging.ui.viewholder.LogViewHolder
import com.f0x1d.logfox.arch.adapter.BaseListAdapter
import com.f0x1d.logfox.feature.logging.databinding.ItemLogBinding
import com.f0x1d.logfox.model.diffCallback
import com.f0x1d.logfox.model.logline.LogLine
import com.f0x1d.logfox.preferences.shared.AppPreferences

class LogsAdapter(
    private val appPreferences: AppPreferences,
    private val selectedItem: (LogLine, Boolean) -> Unit,
    private val copyLog: (LogLine) -> Unit
): BaseListAdapter<LogLine, ItemLogBinding>(diffCallback<LogLine>()) {

    val expandedStates = mutableMapOf<Long, Boolean>()
    var selectedItems = emptySet<LogLine>()
        set(value) {
            field = value
            notifyItemRangeChanged(0, itemCount)
        }

    val textSize get() = appPreferences.logsTextSize.toFloat()
    val logsExpanded get() = appPreferences.logsExpanded
    val logsFormat get() = appPreferences.showLogValues

    override fun createHolder(layoutInflater: LayoutInflater, parent: ViewGroup) = LogViewHolder(
        binding = ItemLogBinding.inflate(layoutInflater, parent, false),
        selectedItem = selectedItem,
        copyLog = copyLog,
    )
}

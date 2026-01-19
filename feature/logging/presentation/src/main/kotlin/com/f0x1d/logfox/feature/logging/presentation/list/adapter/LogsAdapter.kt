package com.f0x1d.logfox.feature.logging.presentation.list.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.f0x1d.logfox.core.presentation.adapter.BaseListAdapter
import com.f0x1d.logfox.feature.logging.presentation.databinding.ItemLogBinding
import com.f0x1d.logfox.feature.logging.presentation.list.viewholder.LogViewHolder
import com.f0x1d.logfox.core.recycler.diffCallback
import com.f0x1d.logfox.feature.logging.api.model.LogLine
import com.f0x1d.logfox.feature.logging.api.model.ShowLogValues

class LogsAdapter(
    private val textSizeProvider: () -> Float,
    private val logsExpandedProvider: () -> Boolean,
    private val logsFormatProvider: () -> ShowLogValues,
    private val selectedItem: (LogLine, Boolean) -> Unit,
    private val copyLog: (LogLine) -> Unit
): BaseListAdapter<LogLine, ItemLogBinding>(diffCallback<LogLine>()) {

    val expandedStates = mutableMapOf<Long, Boolean>()
    var selectedItems = emptySet<LogLine>()
        set(value) {
            field = value
            notifyItemRangeChanged(0, itemCount)
        }

    val textSize get() = textSizeProvider()
    val logsExpanded get() = logsExpandedProvider()
    val logsFormat get() = logsFormatProvider()

    override fun createHolder(layoutInflater: LayoutInflater, parent: ViewGroup) = LogViewHolder(
        binding = ItemLogBinding.inflate(layoutInflater, parent, false),
        selectedItem = selectedItem,
        copyLog = copyLog,
    )
}

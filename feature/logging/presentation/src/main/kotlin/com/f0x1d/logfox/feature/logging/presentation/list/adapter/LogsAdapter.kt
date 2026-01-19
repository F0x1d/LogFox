package com.f0x1d.logfox.feature.logging.presentation.list.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.f0x1d.logfox.core.presentation.adapter.BaseListAdapter
import com.f0x1d.logfox.core.recycler.diffCallback
import com.f0x1d.logfox.feature.logging.api.model.LogLine
import com.f0x1d.logfox.feature.logging.api.model.ShowLogValues
import com.f0x1d.logfox.feature.logging.presentation.databinding.ItemLogBinding
import com.f0x1d.logfox.feature.logging.presentation.list.viewholder.LogViewHolder

class LogsAdapter(
    private val selectedItem: (LogLine, Boolean) -> Unit,
    private val copyLog: (LogLine) -> Unit,
) : BaseListAdapter<LogLine, ItemLogBinding>(diffCallback<LogLine>()) {

    val expandedStates = mutableMapOf<Long, Boolean>()
    var selectedItems = emptySet<LogLine>()
        set(value) {
            field = value
            notifyItemRangeChanged(0, itemCount)
        }

    var textSize: Float = 14f
    var logsExpanded: Boolean = false
    var logsFormat: ShowLogValues = ShowLogValues(
        date = true,
        time = true,
        uid = false,
        pid = true,
        tid = true,
        packageName = false,
        tag = true,
        content = true,
    )

    override fun createHolder(layoutInflater: LayoutInflater, parent: ViewGroup) = LogViewHolder(
        binding = ItemLogBinding.inflate(layoutInflater, parent, false),
        selectedItem = selectedItem,
        copyLog = copyLog,
    )
}

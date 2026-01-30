package com.f0x1d.logfox.feature.logging.presentation.list.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.f0x1d.logfox.core.recycler.adapter.BaseListAdapter
import com.f0x1d.logfox.core.recycler.diffCallback
import com.f0x1d.logfox.feature.logging.presentation.databinding.ItemLogBinding
import com.f0x1d.logfox.feature.logging.presentation.list.model.LogLineItem
import com.f0x1d.logfox.feature.logging.presentation.list.viewholder.LogViewHolder

class LogsAdapter(
    private val selectedItem: (LogLineItem, Boolean) -> Unit,
    private val copyLog: (LogLineItem) -> Unit,
    private val createFilter: (LogLineItem) -> Unit,
) : BaseListAdapter<LogLineItem, ItemLogBinding>(diffCallback<LogLineItem>()) {

    val expandedStates = mutableMapOf<Long, Boolean>()
    var selecting: Boolean = false

    var textSize: Float = 14f
    var logsExpanded: Boolean = false

    override fun createHolder(layoutInflater: LayoutInflater, parent: ViewGroup) = LogViewHolder(
        binding = ItemLogBinding.inflate(layoutInflater, parent, false),
        selectedItem = selectedItem,
        copyLog = copyLog,
        createFilter = createFilter,
    )
}

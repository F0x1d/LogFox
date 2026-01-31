package com.f0x1d.logfox.feature.logging.presentation.list.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.f0x1d.logfox.core.recycler.adapter.BaseListAdapter
import com.f0x1d.logfox.core.recycler.diffCallback
import com.f0x1d.logfox.feature.logging.presentation.databinding.ItemLogBinding
import com.f0x1d.logfox.feature.logging.presentation.list.model.LogLineItem
import com.f0x1d.logfox.feature.logging.presentation.list.viewholder.LogViewHolder

class LogsAdapter(
    private val onClick: (LogLineItem) -> Unit,
    private val onSelectClick: (LogLineItem) -> Unit,
    private val onCopyClick: (LogLineItem) -> Unit,
    private val onCreateFilterClick: (LogLineItem) -> Unit,
) : BaseListAdapter<LogLineItem, ItemLogBinding>(diffCallback<LogLineItem>()) {

    override fun createHolder(layoutInflater: LayoutInflater, parent: ViewGroup) = LogViewHolder(
        binding = ItemLogBinding.inflate(layoutInflater, parent, false),
        onClick = onClick,
        onSelectClick = onSelectClick,
        onCopyClick = onCopyClick,
        onCreateFilterClick = onCreateFilterClick,
    )
}

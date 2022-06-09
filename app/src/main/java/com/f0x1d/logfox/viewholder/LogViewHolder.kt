package com.f0x1d.logfox.viewholder

import com.f0x1d.logfox.adapter.LogsAdapter
import com.f0x1d.logfox.databinding.ItemTextBinding
import com.f0x1d.logfox.logging.model.LogLine
import com.f0x1d.logfox.viewholder.base.BaseViewHolder

class LogViewHolder(binding: ItemTextBinding, longClick: (LogLine) -> Unit): BaseViewHolder<LogLine, ItemTextBinding>(binding) {

    private val currentItem: LogLine
        get() = elements[bindingAdapterPosition]

    init {
        binding.root.setOnClickListener {
            adapter<LogsAdapter>().expandedStates.apply {
                currentItem.id.also {
                    put(it, !getOrPut(it) { false })
                    changeExpanded(it)
                }
            }
        }
        binding.root.setOnLongClickListener {
            longClick.invoke(currentItem)
            return@setOnLongClickListener true
        }
    }

    override fun bindTo(data: LogLine) {
        binding.text.text = data.original
        changeExpanded(data.id)
    }

    private fun changeExpanded(key: Long) {
        binding.text.maxLines = if (adapter<LogsAdapter>().expandedStates.getOrPut(key) { false }) Int.MAX_VALUE else 1
    }
}
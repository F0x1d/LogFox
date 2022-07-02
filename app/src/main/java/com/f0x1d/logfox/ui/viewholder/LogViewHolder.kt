package com.f0x1d.logfox.ui.viewholder

import android.graphics.drawable.ColorDrawable
import androidx.core.graphics.ColorUtils
import com.f0x1d.logfox.adapter.LogsAdapter
import com.f0x1d.logfox.databinding.ItemLogBinding
import com.f0x1d.logfox.model.LogLine
import com.f0x1d.logfox.ui.viewholder.base.BaseViewHolder
import com.google.android.material.color.MaterialColors

class LogViewHolder(binding: ItemLogBinding): BaseViewHolder<LogLine, ItemLogBinding>(binding) {

    private val currentColorPrimary = MaterialColors.getColor(binding.root, com.google.android.material.R.attr.colorPrimary)
    private val background = binding.container.background
    private val selectedBackground = ColorDrawable(ColorUtils.blendARGB((background as ColorDrawable).color, currentColorPrimary, 0.2f))

    init {
        binding.root.setOnClickListener {
            if (adapter<LogsAdapter>().selectedItems.isNotEmpty())
                selectItem()
            else
                expandOrCollapseItem()
        }
        binding.root.setOnLongClickListener {
            if (adapter<LogsAdapter>().selectedItems.isNotEmpty())
                expandOrCollapseItem()
            else
                selectItem()
            return@setOnLongClickListener true
        }
    }

    override fun bindTo(data: LogLine) {
        binding.logText.textSize = adapter<LogsAdapter>().textSize
        binding.logText.text = data.original
        changeExpandedAndSelected(data)
    }

    private fun selectItem() {
        adapter<LogsAdapter>().selectedItems.apply {
            currentItem.also {
                if (any { logLine -> it.id == logLine.id })
                    remove(it)
                else
                    add(it)

                changeExpandedAndSelected(it)
            }
        }
    }

    private fun expandOrCollapseItem() {
        adapter<LogsAdapter>().expandedStates.apply {
            currentItem.also {
                put(it.id, !getOrDefault(it.id, adapter<LogsAdapter>().logsExpanded))
                changeExpandedAndSelected(it)
            }
        }
    }

    private fun changeExpandedAndSelected(logLine: LogLine) {
        binding.logText.maxLines = if (adapter<LogsAdapter>().expandedStates.getOrDefault(logLine.id, adapter<LogsAdapter>().logsExpanded)) Int.MAX_VALUE else 1
        binding.container.background = if (adapter<LogsAdapter>().selectedItems.contains(logLine)) selectedBackground else background
    }
}
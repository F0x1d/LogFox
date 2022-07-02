package com.f0x1d.logfox.ui.viewholder

import android.content.res.ColorStateList
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.GradientDrawable
import androidx.core.graphics.ColorUtils
import com.f0x1d.logfox.adapter.LogsAdapter
import com.f0x1d.logfox.databinding.ItemLogBinding
import com.f0x1d.logfox.extensions.backgroundColorByLevel
import com.f0x1d.logfox.extensions.foregroundColorByLevel
import com.f0x1d.logfox.extensions.logsFormatted
import com.f0x1d.logfox.model.LogLine
import com.f0x1d.logfox.ui.viewholder.base.BaseViewHolder
import com.f0x1d.logfox.utils.dpToPx
import com.google.android.material.color.MaterialColors

class LogViewHolder(binding: ItemLogBinding): BaseViewHolder<LogLine, ItemLogBinding>(binding) {

    private val currentColorPrimary = MaterialColors.getColor(binding.root, com.google.android.material.R.attr.colorPrimary)
    private val background = binding.container.background
    private val selectedBackground = ColorDrawable(ColorUtils.blendARGB((background as ColorDrawable).color, currentColorPrimary, 0.2f))

    private val radius = 6.dpToPx
    private val colorPrimary = MaterialColors.getColor(binding.root, com.google.android.material.R.attr.colorPrimary)

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
        adapter<LogsAdapter>().textSize.also {
            binding.logText.textSize = it
            binding.levelText.textSize = it
        }

        binding.logText.text = buildString {
            adapter<LogsAdapter>().logsFormat.also {
                if (it[0]) append(data.dateAndTime.logsFormatted + " ")
                if (it[1]) append(data.pid + " ")
                if (it[2]) append(data.tid + " ")
                if (it[3]) append(data.tag + ": ")
                if (it[4]) append(data.content)
            }
        }
        binding.levelText.text = data.level.letter

        binding.levelText.background = GradientDrawable().apply {
            cornerRadii = floatArrayOf(0f, 0f, radius, radius, radius, radius, 0f, 0f)
            color = ColorStateList.valueOf(data.level.backgroundColorByLevel(colorPrimary))
        }
        binding.levelText.setTextColor(data.level.foregroundColorByLevel())

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
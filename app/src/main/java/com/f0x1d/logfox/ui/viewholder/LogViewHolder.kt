package com.f0x1d.logfox.ui.viewholder

import android.content.res.ColorStateList
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.GradientDrawable
import android.view.Gravity
import androidx.appcompat.widget.PopupMenu
import androidx.core.graphics.ColorUtils
import com.f0x1d.logfox.R
import com.f0x1d.logfox.adapter.LogsAdapter
import com.f0x1d.logfox.databinding.ItemLogBinding
import com.f0x1d.logfox.extensions.logline.backgroundColorByLevel
import com.f0x1d.logfox.extensions.logline.foregroundColorByLevel
import com.f0x1d.logfox.model.LogLine
import com.f0x1d.logfox.ui.viewholder.base.BaseViewHolder
import com.f0x1d.logfox.utils.dpToPx
import com.google.android.material.color.MaterialColors

class LogViewHolder(
    binding: ItemLogBinding,
    private val copyLog: (LogLine) -> Unit
): BaseViewHolder<LogLine, ItemLogBinding>(binding) {

    private val currentColorPrimary = MaterialColors.getColor(binding.root, com.google.android.material.R.attr.colorPrimary)
    private val background = binding.container.background
    private val selectedBackground = ColorDrawable(ColorUtils.blendARGB((background as ColorDrawable).color, currentColorPrimary, 0.2f))

    private val radius = 6.dpToPx

    private val popupMenu: PopupMenu

    init {
        popupMenu = PopupMenu(binding.root.context, binding.root, Gravity.END)
        popupMenu.inflate(R.menu.log_menu)
        popupMenu.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.select_item -> {
                    selectItem()
                    true
                }
                R.id.copy_item -> {
                    copyLog.invoke(currentItem)
                    true
                }

                else -> false
            }
        }
        popupMenu.setForceShowIcon(true)

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
                popupMenu.show()
            return@setOnLongClickListener true
        }
    }

    override fun bindTo(data: LogLine) {
        adapter<LogsAdapter>().textSize.also {
            binding.logText.textSize = it
            binding.levelText.textSize = it
        }

        binding.logText.text = buildString {
            adapter<LogsAdapter>().logsFormat.apply {
                if (date) append(data.logsDateFormatted + " ")
                if (time) append(data.logsTimeFormatted + " ")
                if (uid) append(data.uid + " ")
                if (pid) append(data.pid + " ")
                if (tid) append(data.tid + " ")
                if (packageName && data.packageName != null) append(data.packageName + " ")
                if (tag) append(data.tag + if (content) ": " else "")
                if (content) append(data.content)
            }
        }
        binding.levelText.text = data.level.letter
        val context = binding.levelText.context
        binding.levelText.background = GradientDrawable().apply {
            cornerRadii = floatArrayOf(0f, 0f, radius, radius, radius, radius, 0f, 0f)
            color = ColorStateList.valueOf(data.level.backgroundColorByLevel(context))
        }
        binding.levelText.setTextColor(data.level.foregroundColorByLevel(context))

        changeExpandedAndSelected(data)
    }

    override fun detach() {
        popupMenu.dismiss()
    }

    private fun selectItem() = adapter<LogsAdapter>().selectedItems.apply {
        currentItem.also {
            if (any { logLine -> it.id == logLine.id })
                remove(it)
            else
                add(it)

            changeExpandedAndSelected(it)
        }
    }

    private fun expandOrCollapseItem() = adapter<LogsAdapter>().expandedStates.apply {
        currentItem.also {
            put(it.id, !getOrElse(it.id) { adapter<LogsAdapter>().logsExpanded })
            changeExpandedAndSelected(it)
        }
    }

    private fun changeExpandedAndSelected(logLine: LogLine) {
        binding.logText.maxLines = if (adapter<LogsAdapter>().expandedStates.getOrElse(logLine.id) { adapter<LogsAdapter>().logsExpanded }) Int.MAX_VALUE else 1
        binding.container.background = if (adapter<LogsAdapter>().selectedItems.contains(logLine)) selectedBackground else background
    }
}
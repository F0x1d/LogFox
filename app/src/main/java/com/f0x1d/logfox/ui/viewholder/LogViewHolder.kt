package com.f0x1d.logfox.ui.viewholder

import android.content.res.ColorStateList
import android.view.Gravity
import androidx.appcompat.widget.PopupMenu
import com.f0x1d.logfox.R
import com.f0x1d.logfox.adapter.LogsAdapter
import com.f0x1d.logfox.databinding.ItemLogBinding
import com.f0x1d.logfox.extensions.logline.backgroundColorByLevel
import com.f0x1d.logfox.extensions.logline.foregroundColorByLevel
import com.f0x1d.logfox.model.LogLine
import com.f0x1d.logfox.ui.viewholder.base.BaseViewHolder
import com.f0x1d.logfox.utils.dpToPx

class LogViewHolder(
    binding: ItemLogBinding,
    private val copyLog: (LogLine) -> Unit
): BaseViewHolder<LogLine, ItemLogBinding>(binding) {

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
                    copyLog.invoke(currentItem ?: return@setOnMenuItemClickListener false)
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

        binding.levelText.backgroundTintList = ColorStateList.valueOf(data.level.backgroundColorByLevel())
        binding.levelText.setTextColor(data.level.foregroundColorByLevel())

        changeExpandedAndSelected(data)
    }

    override fun detach() {
        popupMenu.dismiss()
    }

    private fun selectItem() = adapter<LogsAdapter>().selectedItems.apply {
        currentItem?.also {
            if (any { logLine -> it.id == logLine.id })
                remove(it)
            else
                add(it)

            changeExpandedAndSelected(it)
        }
    }

    private fun expandOrCollapseItem() = adapter<LogsAdapter>().expandedStates.apply {
        currentItem?.also {
            put(it.id, !getOrElse(it.id) { adapter<LogsAdapter>().logsExpanded })
            changeExpandedAndSelected(it)
        }
    }

    private fun changeExpandedAndSelected(logLine: LogLine) {
        binding.logText.maxLines = if (adapter<LogsAdapter>().expandedStates.getOrElse(logLine.id) { adapter<LogsAdapter>().logsExpanded }) Int.MAX_VALUE else 1
        binding.container.isSelected = adapter<LogsAdapter>().selectedItems.contains(logLine)
    }
}
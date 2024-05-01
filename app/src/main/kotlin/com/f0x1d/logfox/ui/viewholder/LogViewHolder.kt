package com.f0x1d.logfox.ui.viewholder

import android.view.Gravity
import androidx.appcompat.widget.PopupMenu
import com.f0x1d.logfox.R
import com.f0x1d.logfox.adapter.LogsAdapter
import com.f0x1d.logfox.databinding.ItemLogBinding
import com.f0x1d.logfox.extensions.context.dateTimeFormatter
import com.f0x1d.logfox.ui.viewholder.base.BaseViewHolder

class LogViewHolder(
    binding: ItemLogBinding,
    private val selectedItem: (com.f0x1d.logfox.model.LogLine, Boolean) -> Unit,
    private val copyLog: (com.f0x1d.logfox.model.LogLine) -> Unit
): BaseViewHolder<com.f0x1d.logfox.model.LogLine, ItemLogBinding>(binding) {

    private val dateTimeFormatter = binding.root.context.dateTimeFormatter

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
                    copyLog(currentItem ?: return@setOnMenuItemClickListener false)
                    true
                }

                else -> false
            }
        }
        popupMenu.setForceShowIcon(true)

        binding.apply {
            root.setOnClickListener {
                val adapter = adapter<LogsAdapter>() ?: return@setOnClickListener

                if (adapter.selectedItems.isNotEmpty())
                    selectItem()
                else
                    expandOrCollapseItem()
            }
            root.setOnLongClickListener {
                val adapter = adapter<LogsAdapter>() ?: return@setOnLongClickListener true

                if (adapter.selectedItems.isNotEmpty())
                    expandOrCollapseItem()
                else
                    popupMenu.show()

                return@setOnLongClickListener true
            }
        }
    }

    override fun ItemLogBinding.bindTo(data: com.f0x1d.logfox.model.LogLine) {
        adapter<LogsAdapter>()?.textSize?.also {
            logText.textSize = it
            levelView.textSize = it
        }

        logText.text = buildString {
            adapter<LogsAdapter>()?.logsFormat?.apply {
                if (date) append(dateTimeFormatter.formatDate(data.dateAndTime) + " ")
                if (time) append(dateTimeFormatter.formatTime(data.dateAndTime) + " ")
                if (uid) append(data.uid + " ")
                if (pid) append(data.pid + " ")
                if (tid) append(data.tid + " ")
                if (packageName && data.packageName != null) append(data.packageName + " ")
                if (tag) append(data.tag + if (content) ": " else "")
                if (content) append(data.content)
            }
        }

        levelView.logLevel = data.level

        changeExpandedAndSelected(data)
    }

    override fun ItemLogBinding.detach() {
        popupMenu.dismiss()
    }

    private fun selectItem() = adapter<LogsAdapter>()?.selectedItems?.apply {
        currentItem?.also {
            val newValue = any { logLine -> it.id == logLine.id }.not()
            selectedItem(it, newValue)
        }
    }

    private fun ItemLogBinding.expandOrCollapseItem() = adapter<LogsAdapter>()?.apply {
        expandedStates.apply {
            currentItem?.also {
                val newValue = getOrElse(it.id) { logsExpanded }.not()

                put(it.id, newValue)
                changeExpandedAndSelected(it)
            }
        }
    }

    private fun ItemLogBinding.changeExpandedAndSelected(logLine: com.f0x1d.logfox.model.LogLine) = adapter<LogsAdapter>()?.apply {
        val expanded = expandedStates.getOrElse(logLine.id) { logsExpanded }

        logText.maxLines = if (expanded) Int.MAX_VALUE else 1
        container.isSelected = selectedItems.contains(logLine)
    }
}

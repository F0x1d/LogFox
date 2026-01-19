package com.f0x1d.logfox.feature.logging.presentation.list.viewholder

import android.view.Gravity
import androidx.appcompat.widget.PopupMenu
import com.f0x1d.logfox.core.presentation.viewholder.BaseViewHolder
import com.f0x1d.logfox.feature.datetime.impl.dateTimeFormatter
import com.f0x1d.logfox.feature.logging.api.model.LogLine
import com.f0x1d.logfox.feature.logging.presentation.R
import com.f0x1d.logfox.feature.logging.presentation.databinding.ItemLogBinding
import com.f0x1d.logfox.feature.logging.presentation.list.adapter.LogsAdapter

class LogViewHolder(
    binding: ItemLogBinding,
    private val selectedItem: (LogLine, Boolean) -> Unit,
    private val copyLog: (LogLine) -> Unit,
) : BaseViewHolder<LogLine, ItemLogBinding>(binding) {

    private val dateTimeFormatter = binding.root.context.dateTimeFormatter

    private val popupMenu: PopupMenu = PopupMenu(
        binding.root.context,
        binding.root,
        Gravity.END,
    ).apply {
        inflate(R.menu.log_menu)
        setForceShowIcon(true)

        setOnMenuItemClickListener {
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
    }

    init {
        binding.apply {
            root.setOnClickListener {
                val adapter = adapter<LogsAdapter>() ?: return@setOnClickListener

                if (adapter.selectedItems.isNotEmpty()) {
                    selectItem()
                } else {
                    expandOrCollapseItem()
                }
            }
            root.setOnLongClickListener {
                val adapter = adapter<LogsAdapter>() ?: return@setOnLongClickListener true

                if (adapter.selectedItems.isNotEmpty()) {
                    expandOrCollapseItem()
                } else {
                    popupMenu.show()
                }

                return@setOnLongClickListener true
            }
        }
    }

    override fun ItemLogBinding.bindTo(data: LogLine) {
        adapter<LogsAdapter>()?.textSize?.also {
            logText.textSize = it
            levelView.textSize = it
        }

        adapter<LogsAdapter>()?.logsFormat?.let { values ->
            logText.text = data.formatOriginal(
                values = values,
                formatDate = dateTimeFormatter::formatDate,
                formatTime = dateTimeFormatter::formatTime,
            )
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

    private fun ItemLogBinding.changeExpandedAndSelected(logLine: LogLine) = adapter<LogsAdapter>()?.apply {
        val expanded = expandedStates.getOrElse(logLine.id) { logsExpanded }

        logText.maxLines = if (expanded) Int.MAX_VALUE else 1
        container.isSelected = selectedItems.contains(logLine)
    }
}

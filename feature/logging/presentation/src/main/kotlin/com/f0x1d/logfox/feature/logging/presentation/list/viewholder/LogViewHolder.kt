package com.f0x1d.logfox.feature.logging.presentation.list.viewholder

import android.view.Gravity
import androidx.appcompat.widget.PopupMenu
import com.f0x1d.logfox.core.recycler.viewholder.BaseViewHolder
import com.f0x1d.logfox.feature.logging.presentation.R
import com.f0x1d.logfox.feature.logging.presentation.databinding.ItemLogBinding
import com.f0x1d.logfox.feature.logging.presentation.list.adapter.LogsAdapter
import com.f0x1d.logfox.feature.logging.presentation.list.model.LogLineItem

class LogViewHolder(
    binding: ItemLogBinding,
    private val selectedItem: (LogLineItem, Boolean) -> Unit,
    private val copyLog: (LogLineItem) -> Unit,
    private val createFilter: (LogLineItem) -> Unit,
) : BaseViewHolder<LogLineItem, ItemLogBinding>(binding) {

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

                R.id.create_filter_item -> {
                    createFilter(currentItem ?: return@setOnMenuItemClickListener false)
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

                if (adapter.selecting) {
                    selectItem()
                } else {
                    expandOrCollapseItem()
                }
            }
            root.setOnLongClickListener {
                val adapter = adapter<LogsAdapter>() ?: return@setOnLongClickListener true

                if (adapter.selecting) {
                    expandOrCollapseItem()
                } else {
                    popupMenu.show()
                }

                return@setOnLongClickListener true
            }
        }
    }

    override fun ItemLogBinding.bindTo(data: LogLineItem) {
        adapter<LogsAdapter>()?.textSize?.also {
            logText.textSize = it
            levelView.textSize = it
        }

        logText.text = data.displayText

        levelView.logLevel = data.level

        changeExpandedAndSelected(data)
    }

    override fun ItemLogBinding.detach() {
        popupMenu.dismiss()
    }

    private fun selectItem() {
        currentItem?.also {
            selectedItem(it, !it.selected)
        }
    }

    private fun ItemLogBinding.expandOrCollapseItem() = adapter<LogsAdapter>()?.apply {
        expandedStates.apply {
            currentItem?.also {
                val newValue = getOrElse(it.logLineId) { logsExpanded }.not()

                put(it.logLineId, newValue)
                changeExpandedAndSelected(it)
            }
        }
    }

    private fun ItemLogBinding.changeExpandedAndSelected(item: LogLineItem) = adapter<LogsAdapter>()?.apply {
        val expanded = expandedStates.getOrElse(item.logLineId) { logsExpanded }

        logText.maxLines = if (expanded) Int.MAX_VALUE else 1
        container.isSelected = item.selected
    }
}

package com.f0x1d.logfox.feature.logging.presentation.list.viewholder

import android.view.Gravity
import androidx.appcompat.widget.PopupMenu
import com.f0x1d.logfox.core.recycler.viewholder.BaseViewHolder
import com.f0x1d.logfox.feature.logging.presentation.R
import com.f0x1d.logfox.feature.logging.presentation.databinding.ItemLogBinding
import com.f0x1d.logfox.feature.logging.presentation.list.model.LogLineItem

class LogViewHolder(
    binding: ItemLogBinding,
    private val onClick: (LogLineItem) -> Unit,
    private val onSelectClick: (LogLineItem) -> Unit,
    private val onCopyClick: (LogLineItem) -> Unit,
    private val onCreateFilterClick: (LogLineItem) -> Unit,
) : BaseViewHolder<LogLineItem, ItemLogBinding>(binding) {

    private val popupMenu: PopupMenu = PopupMenu(
        binding.root.context,
        binding.root,
        Gravity.END,
    ).apply {
        inflate(R.menu.log_menu)
        setForceShowIcon(true)

        setOnMenuItemClickListener {
            val item = currentItem ?: return@setOnMenuItemClickListener false

            when (it.itemId) {
                R.id.select_item -> {
                    onSelectClick(item)
                    true
                }

                R.id.copy_item -> {
                    onCopyClick(item)
                    true
                }

                R.id.create_filter_item -> {
                    onCreateFilterClick(item)
                    true
                }

                else -> false
            }
        }
    }

    init {
        binding.apply {
            root.setOnClickListener {
                val item = currentItem ?: return@setOnClickListener
                onClick(item)
            }
            root.setOnLongClickListener {
                popupMenu.show()
                return@setOnLongClickListener true
            }
        }
    }

    override fun ItemLogBinding.bindTo(data: LogLineItem) {
        logText.textSize = data.textSize
        levelView.textSize = data.textSize

        logText.text = data.displayText

        levelView.logLevel = data.level

        logText.maxLines = if (data.expanded) Int.MAX_VALUE else 1
        container.isSelected = data.selected
    }

    override fun ItemLogBinding.detach() {
        popupMenu.dismiss()
    }
}

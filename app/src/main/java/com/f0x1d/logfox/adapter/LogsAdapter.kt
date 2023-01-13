package com.f0x1d.logfox.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.f0x1d.logfox.adapter.base.BaseAdapter
import com.f0x1d.logfox.databinding.ItemLogBinding
import com.f0x1d.logfox.model.LogLine
import com.f0x1d.logfox.ui.viewholder.LogViewHolder
import com.f0x1d.logfox.utils.preferences.AppPreferences

class LogsAdapter(private val appPreferences: AppPreferences): BaseAdapter<LogLine, ItemLogBinding>() {

    override val updateWhenSet = false

    val expandedStates = mutableMapOf<Long, Boolean>()
    val selectedItems = mutableListOf<LogLine>()

    var textSize = appPreferences.logsTextSize.toFloat()
        set(value) {
            field = value
            notifyItemRangeChanged(0, itemCount)
        }
    var logsExpanded = appPreferences.logsExpanded
        set(value) {
            field = value
            notifyItemRangeChanged(0, itemCount)
        }
    var logsFormat = appPreferences.showLogValues
        set(value) {
            field = value
            notifyItemRangeChanged(0, itemCount)
        }

    override fun createHolder(layoutInflater: LayoutInflater, parent: ViewGroup) = LogViewHolder(
        ItemLogBinding.inflate(layoutInflater, parent, false)
    )

    override fun getItemId(position: Int) = elements[position].id

    fun clearSelected() {
        selectedItems.clear()
        notifyItemRangeChanged(0, itemCount)
    }
}
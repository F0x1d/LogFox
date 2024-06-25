package com.f0x1d.logfox.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import com.f0x1d.logfox.arch.adapter.BaseListAdapter
import com.f0x1d.logfox.database.entity.UserFilter
import com.f0x1d.logfox.databinding.ItemFilterBinding
import com.f0x1d.logfox.ui.viewholder.FilterViewHolder

class FiltersAdapter(
    private val click: (UserFilter) -> Unit,
    private val delete: (UserFilter) -> Unit,
    private val checked: (UserFilter, Boolean) -> Unit
): BaseListAdapter<UserFilter, ItemFilterBinding>(FILTER_DIFF) {

    companion object {
        private val FILTER_DIFF = object : DiffUtil.ItemCallback<UserFilter>() {
            override fun areItemsTheSame(oldItem: UserFilter, newItem: UserFilter) = oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: UserFilter, newItem: UserFilter) = oldItem == newItem
        }
    }

    override fun createHolder(layoutInflater: LayoutInflater, parent: ViewGroup) = FilterViewHolder(
        binding = ItemFilterBinding.inflate(layoutInflater, parent, false),
        click = click,
        delete = delete,
        checked = checked
    )
}

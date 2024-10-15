package com.f0x1d.logfox.feature.filters.impl.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.f0x1d.logfox.arch.adapter.BaseListAdapter
import com.f0x1d.logfox.database.entity.UserFilter
import com.f0x1d.logfox.feature.filters.impl.databinding.ItemFilterBinding
import com.f0x1d.logfox.feature.filters.impl.ui.viewholder.FilterViewHolder
import com.f0x1d.logfox.model.diffCallback

class FiltersAdapter(
    private val click: (UserFilter) -> Unit,
    private val delete: (UserFilter) -> Unit,
    private val checked: (UserFilter, Boolean) -> Unit
): BaseListAdapter<UserFilter, ItemFilterBinding>(diffCallback<UserFilter>()) {

    override fun createHolder(layoutInflater: LayoutInflater, parent: ViewGroup) = FilterViewHolder(
        binding = ItemFilterBinding.inflate(layoutInflater, parent, false),
        click = click,
        delete = delete,
        checked = checked,
    )
}

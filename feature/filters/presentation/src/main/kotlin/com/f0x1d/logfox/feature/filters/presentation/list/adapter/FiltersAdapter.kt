package com.f0x1d.logfox.feature.filters.presentation.list.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.f0x1d.logfox.core.presentation.adapter.BaseListAdapter
import com.f0x1d.logfox.core.recycler.diffCallback
import com.f0x1d.logfox.feature.database.model.UserFilter
import com.f0x1d.logfox.feature.filters.presentation.databinding.ItemFilterBinding
import com.f0x1d.logfox.feature.filters.presentation.list.viewholder.FilterViewHolder

class FiltersAdapter(
    private val click: (UserFilter) -> Unit,
    private val delete: (UserFilter) -> Unit,
    private val checked: (UserFilter, Boolean) -> Unit,
) : BaseListAdapter<UserFilter, ItemFilterBinding>(diffCallback<UserFilter>()) {

    override fun createHolder(layoutInflater: LayoutInflater, parent: ViewGroup) = FilterViewHolder(
        binding = ItemFilterBinding.inflate(layoutInflater, parent, false),
        click = click,
        delete = delete,
        checked = checked,
    )
}

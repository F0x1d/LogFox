package com.f0x1d.logfox.feature.filters.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.f0x1d.logfox.arch.adapter.BaseListAdapter
import com.f0x1d.logfox.feature.filters.databinding.ItemAppBinding
import com.f0x1d.logfox.feature.filters.ui.viewholder.AppViewHolder
import com.f0x1d.logfox.model.InstalledApp
import com.f0x1d.logfox.model.diffCallback

class AppsAdapter(
    private val click: (InstalledApp) -> Unit
): BaseListAdapter<InstalledApp, ItemAppBinding>(diffCallback<InstalledApp>()) {

    override fun createHolder(layoutInflater: LayoutInflater, parent: ViewGroup) = AppViewHolder(
        binding = ItemAppBinding.inflate(layoutInflater, parent, false),
        click = click,
    )
}

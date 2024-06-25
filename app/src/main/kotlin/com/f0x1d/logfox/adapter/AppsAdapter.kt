package com.f0x1d.logfox.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import com.f0x1d.logfox.arch.adapter.BaseListAdapter
import com.f0x1d.logfox.databinding.ItemAppBinding
import com.f0x1d.logfox.model.InstalledApp
import com.f0x1d.logfox.ui.viewholder.AppViewHolder

class AppsAdapter(
    private val click: (com.f0x1d.logfox.model.InstalledApp) -> Unit
): BaseListAdapter<InstalledApp, ItemAppBinding>(APP_DIFF) {

    companion object {
        val APP_DIFF = object : DiffUtil.ItemCallback<com.f0x1d.logfox.model.InstalledApp>() {
            override fun areItemsTheSame(oldItem: com.f0x1d.logfox.model.InstalledApp, newItem: com.f0x1d.logfox.model.InstalledApp) = oldItem.packageName == newItem.packageName

            override fun areContentsTheSame(oldItem: com.f0x1d.logfox.model.InstalledApp, newItem: com.f0x1d.logfox.model.InstalledApp) = oldItem == newItem
        }
    }

    override fun createHolder(layoutInflater: LayoutInflater, parent: ViewGroup) = AppViewHolder(
        binding = ItemAppBinding.inflate(layoutInflater, parent, false),
        click = click
    )
}

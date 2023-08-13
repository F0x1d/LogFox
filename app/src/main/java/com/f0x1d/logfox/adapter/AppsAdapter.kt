package com.f0x1d.logfox.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import com.f0x1d.logfox.adapter.base.BaseListAdapter
import com.f0x1d.logfox.databinding.ItemAppBinding
import com.f0x1d.logfox.model.InstalledApp
import com.f0x1d.logfox.ui.viewholder.AppViewHolder

class AppsAdapter(
    private val click: (InstalledApp) -> Unit
): BaseListAdapter<InstalledApp, ItemAppBinding>(APP_DIFF) {

    companion object {
        val APP_DIFF = object : DiffUtil.ItemCallback<InstalledApp>() {
            override fun areItemsTheSame(oldItem: InstalledApp, newItem: InstalledApp) = oldItem.packageName == newItem.packageName

            override fun areContentsTheSame(oldItem: InstalledApp, newItem: InstalledApp) = oldItem == newItem
        }
    }

    override fun createHolder(
        layoutInflater: LayoutInflater,
        parent: ViewGroup
    ) = AppViewHolder(
        ItemAppBinding.inflate(layoutInflater, parent, false),
        click
    )
}
package com.f0x1d.logfox.feature.crashes.presentation.common.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import com.f0x1d.logfox.core.recycler.adapter.BaseListAdapter
import com.f0x1d.logfox.feature.crashes.api.model.AppCrashesCount
import com.f0x1d.logfox.feature.crashes.presentation.common.viewholder.CrashViewHolder
import com.f0x1d.logfox.feature.crashes.presentation.databinding.ItemCrashBinding

class CrashesAdapter(
    private val click: (AppCrashesCount) -> Unit,
    private val delete: (AppCrashesCount) -> Unit,
) : BaseListAdapter<AppCrashesCount, ItemCrashBinding>(CRASH_DIFF) {

    companion object {
        private val CRASH_DIFF = object : DiffUtil.ItemCallback<AppCrashesCount>() {
            override fun areItemsTheSame(oldItem: AppCrashesCount, newItem: AppCrashesCount) = oldItem.lastCrash.id == newItem.lastCrash.id

            override fun areContentsTheSame(oldItem: AppCrashesCount, newItem: AppCrashesCount) = oldItem == newItem
        }
    }

    override fun createHolder(layoutInflater: LayoutInflater, parent: ViewGroup) = CrashViewHolder(
        binding = ItemCrashBinding.inflate(layoutInflater, parent, false),
        click = click,
        delete = delete,
    )
}

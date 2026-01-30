package com.f0x1d.logfox.feature.crashes.presentation.common.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import com.f0x1d.logfox.core.recycler.adapter.BaseListAdapter
import com.f0x1d.logfox.feature.crashes.presentation.common.model.AppCrashesCountItem
import com.f0x1d.logfox.feature.crashes.presentation.common.viewholder.CrashViewHolder
import com.f0x1d.logfox.feature.crashes.presentation.databinding.ItemCrashBinding

class CrashesAdapter(
    private val click: (AppCrashesCountItem) -> Unit,
    private val delete: (AppCrashesCountItem) -> Unit,
) : BaseListAdapter<AppCrashesCountItem, ItemCrashBinding>(CRASH_DIFF) {

    companion object {
        private val CRASH_DIFF = object : DiffUtil.ItemCallback<AppCrashesCountItem>() {
            override fun areItemsTheSame(oldItem: AppCrashesCountItem, newItem: AppCrashesCountItem) =
                oldItem.lastCrashId == newItem.lastCrashId

            override fun areContentsTheSame(oldItem: AppCrashesCountItem, newItem: AppCrashesCountItem) = oldItem == newItem
        }
    }

    override fun createHolder(layoutInflater: LayoutInflater, parent: ViewGroup) = CrashViewHolder(
        binding = ItemCrashBinding.inflate(layoutInflater, parent, false),
        click = click,
        delete = delete,
    )
}

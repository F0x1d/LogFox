package com.f0x1d.logfox.feature.crashes.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import com.f0x1d.logfox.arch.adapter.BaseListAdapter
import com.f0x1d.logfox.database.entity.AppCrashesCount
import com.f0x1d.logfox.feature.crashes.databinding.ItemCrashBinding
import com.f0x1d.logfox.feature.crashes.ui.viewholder.CrashViewHolder

class CrashesAdapter(
    private val click: (AppCrashesCount) -> Unit,
    private val delete: (AppCrashesCount) -> Unit,
): BaseListAdapter<AppCrashesCount, ItemCrashBinding>(CRASH_DIFF) {

    companion object {
        private val CRASH_DIFF = object : DiffUtil.ItemCallback<AppCrashesCount>() {
            override fun areItemsTheSame(oldItem: AppCrashesCount, newItem: AppCrashesCount) =
                oldItem.lastCrash.id == newItem.lastCrash.id

            override fun areContentsTheSame(oldItem: AppCrashesCount, newItem: AppCrashesCount) =
                oldItem == newItem
        }
    }

    override fun createHolder(layoutInflater: LayoutInflater, parent: ViewGroup) = CrashViewHolder(
        binding = ItemCrashBinding.inflate(layoutInflater, parent, false),
        click = click,
        delete = delete,
    )
}

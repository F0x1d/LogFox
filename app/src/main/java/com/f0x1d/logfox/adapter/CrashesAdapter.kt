package com.f0x1d.logfox.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import com.f0x1d.logfox.adapter.base.BaseListAdapter
import com.f0x1d.logfox.database.AppCrash
import com.f0x1d.logfox.databinding.ItemCrashBinding
import com.f0x1d.logfox.ui.viewholder.CrashViewHolder

class CrashesAdapter(
    private val click: (AppCrash) -> Unit,
    private val delete: (AppCrash) -> Unit
): BaseListAdapter<AppCrash, ItemCrashBinding>(CRASH_DIFF) {

    companion object {
        private val CRASH_DIFF = object : DiffUtil.ItemCallback<AppCrash>() {
            override fun areItemsTheSame(oldItem: AppCrash, newItem: AppCrash) = oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: AppCrash, newItem: AppCrash) = oldItem == newItem
        }
    }

    override fun createHolder(layoutInflater: LayoutInflater, parent: ViewGroup) = CrashViewHolder(
        binding = ItemCrashBinding.inflate(layoutInflater, parent, false),
        click = click,
        delete = delete
    )
}
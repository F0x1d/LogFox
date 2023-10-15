package com.f0x1d.logfox.ui.viewholder

import android.annotation.SuppressLint
import com.bumptech.glide.Glide
import com.f0x1d.logfox.R
import com.f0x1d.logfox.databinding.ItemCrashBinding
import com.f0x1d.logfox.extensions.toLocaleString
import com.f0x1d.logfox.extensions.views.widgets.loadIcon
import com.f0x1d.logfox.model.AppCrashesCount
import com.f0x1d.logfox.ui.viewholder.base.BaseViewHolder

class CrashViewHolder(
    binding: ItemCrashBinding,
    click: (AppCrashesCount) -> Unit,
    delete: (AppCrashesCount) -> Unit
): BaseViewHolder<AppCrashesCount, ItemCrashBinding>(binding) {

    init {
        binding.root.setOnClickListener {
            click.invoke(currentItem ?: return@setOnClickListener)
        }
        binding.deleteButton.setOnClickListener {
            delete.invoke(currentItem ?: return@setOnClickListener)
        }
    }

    @SuppressLint("SetTextI18n")
    override fun bindTo(data: AppCrashesCount) {
        binding.icon.loadIcon(data.lastCrash.packageName)

        binding.title.text = data.lastCrash.appName ?: data.lastCrash.packageName

        binding.dateText.text = when (data.count) {
            1 -> "${data.lastCrash.crashType.readableName} • ${data.lastCrash.dateAndTime.toLocaleString()}"

            else -> "${binding.root.context.getString(R.string.crashes)}: ${data.count} • ${data.lastCrash.packageName}"
        }
    }

    override fun recycle() = Glide.with(binding.icon).clear(binding.icon)
}
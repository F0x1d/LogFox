package com.f0x1d.logfox.ui.viewholder

import android.annotation.SuppressLint
import com.bumptech.glide.Glide
import com.f0x1d.logfox.database.AppCrash
import com.f0x1d.logfox.databinding.ItemCrashBinding
import com.f0x1d.logfox.extensions.loadIcon
import com.f0x1d.logfox.extensions.toLocaleString
import com.f0x1d.logfox.ui.viewholder.base.BaseViewHolder

class CrashViewHolder(binding: ItemCrashBinding, click: (AppCrash) -> Unit): BaseViewHolder<AppCrash, ItemCrashBinding>(binding) {

    init {
        binding.root.setOnClickListener {
            click.invoke(elements[bindingAdapterPosition])
        }
    }

    @SuppressLint("SetTextI18n")
    override fun bindTo(data: AppCrash) {
        binding.icon.loadIcon(data.packageName)
        binding.title.text = data.appName ?: data.packageName
        binding.dateText.text = "${data.crashType.readableName} • ${data.dateAndTime.toLocaleString()}"
    }

    override fun recycle() = Glide.with(binding.icon).clear(binding.icon)
}
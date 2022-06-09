package com.f0x1d.logfox.viewholder

import android.annotation.SuppressLint
import com.bumptech.glide.Glide
import com.f0x1d.logfox.R
import com.f0x1d.logfox.adapter.CrashesAdapter
import com.f0x1d.logfox.database.AppCrash
import com.f0x1d.logfox.databinding.ItemCrashBinding
import com.f0x1d.logfox.viewholder.base.BaseViewHolder
import java.util.*

class CrashViewHolder(binding: ItemCrashBinding, click: (AppCrash) -> Unit): BaseViewHolder<AppCrash, ItemCrashBinding>(binding) {

    init {
        binding.root.setOnClickListener {
            click.invoke(elements[bindingAdapterPosition])
        }
    }

    @SuppressLint("SetTextI18n")
    override fun bindTo(data: AppCrash) {
        Glide
            .with(binding.icon)
            .load("icon:${data.packageName}")
            .error(R.drawable.ic_bug)
            .into(binding.icon)

        binding.title.text = data.appName ?: data.packageName
        binding.dateText.text = "${data.crashType.readableName} • ${Date(data.dateAndTime).toLocaleString()}"
    }

    override fun recycle() = Glide.with(binding.icon).clear(binding.icon)
}
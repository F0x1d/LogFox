package com.f0x1d.logfox.feature.filters.ui.viewholder

import com.bumptech.glide.Glide
import com.f0x1d.logfox.arch.ui.viewholder.BaseViewHolder
import com.f0x1d.logfox.feature.filters.databinding.ItemAppBinding
import com.f0x1d.logfox.model.InstalledApp
import com.f0x1d.logfox.ui.view.loadIcon

class AppViewHolder(
    binding: ItemAppBinding,
    click: (InstalledApp) -> Unit
): BaseViewHolder<InstalledApp, ItemAppBinding>(binding) {

    init {
        binding.apply {
            root.setOnClickListener {
                click(currentItem ?: return@setOnClickListener)
            }
        }
    }

    override fun ItemAppBinding.bindTo(data: InstalledApp) {
        icon.loadIcon(data.packageName)

        title.text = data.title
        packageNameText.text = data.packageName
    }

    override fun ItemAppBinding.recycle() = Glide.with(icon).clear(icon)
}

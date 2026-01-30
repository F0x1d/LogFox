package com.f0x1d.logfox.feature.crashes.presentation.common.viewholder

import android.annotation.SuppressLint
import com.bumptech.glide.Glide
import com.f0x1d.logfox.core.recycler.viewholder.BaseViewHolder
import com.f0x1d.logfox.core.ui.glide.loadIcon
import com.f0x1d.logfox.feature.crashes.presentation.common.model.AppCrashesCountItem
import com.f0x1d.logfox.feature.crashes.presentation.databinding.ItemCrashBinding
import com.f0x1d.logfox.feature.strings.Strings

class CrashViewHolder(
    binding: ItemCrashBinding,
    click: (AppCrashesCountItem) -> Unit,
    delete: (AppCrashesCountItem) -> Unit,
) : BaseViewHolder<AppCrashesCountItem, ItemCrashBinding>(binding) {

    init {
        binding.apply {
            root.setOnClickListener {
                click(currentItem ?: return@setOnClickListener)
            }
            deleteButton.setOnClickListener {
                delete(currentItem ?: return@setOnClickListener)
            }
        }
    }

    @SuppressLint("SetTextI18n")
    override fun ItemCrashBinding.bindTo(data: AppCrashesCountItem) {
        icon.loadIcon(data.packageName)

        title.text = data.appName ?: data.packageName

        dateText.text = when (data.count) {
            1 -> "${data.crashType.readableName} • ${data.formattedDate}"

            else -> "${root.context.getString(
                Strings.crashes,
            )}: ${data.count} • ${data.packageName}"
        }
    }

    override fun ItemCrashBinding.recycle() = Glide.with(icon).clear(icon)
}

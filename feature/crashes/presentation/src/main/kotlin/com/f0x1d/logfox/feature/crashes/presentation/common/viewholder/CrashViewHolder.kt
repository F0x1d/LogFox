package com.f0x1d.logfox.feature.crashes.presentation.common.viewholder

import android.annotation.SuppressLint
import com.bumptech.glide.Glide
import com.f0x1d.logfox.core.presentation.view.loadIcon
import com.f0x1d.logfox.core.presentation.viewholder.BaseViewHolder
import com.f0x1d.logfox.feature.crashes.presentation.databinding.ItemCrashBinding
import com.f0x1d.logfox.feature.database.model.AppCrashesCount
import com.f0x1d.logfox.feature.strings.Strings
import java.util.Date

class CrashViewHolder(
    binding: ItemCrashBinding,
    click: (AppCrashesCount) -> Unit,
    delete: (AppCrashesCount) -> Unit,
) : BaseViewHolder<AppCrashesCount, ItemCrashBinding>(binding) {

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
    override fun ItemCrashBinding.bindTo(data: AppCrashesCount) {
        icon.loadIcon(data.lastCrash.packageName)

        title.text = data.lastCrash.appName ?: data.lastCrash.packageName

        val localeString = Date(data.lastCrash.dateAndTime).toLocaleString()

        dateText.text = when (data.count) {
            1 -> "${data.lastCrash.crashType.readableName} • $localeString"

            else -> "${root.context.getString(
                Strings.crashes,
            )}: ${data.count} • ${data.lastCrash.packageName}"
        }
    }

    override fun ItemCrashBinding.recycle() = Glide.with(icon).clear(icon)
}

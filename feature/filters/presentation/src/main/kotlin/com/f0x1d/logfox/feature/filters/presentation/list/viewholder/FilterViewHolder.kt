package com.f0x1d.logfox.feature.filters.presentation.list.viewholder

import android.text.Html
import android.view.View
import android.widget.TextView
import com.f0x1d.logfox.core.presentation.view.OnlyUserCheckedChangeListener
import com.f0x1d.logfox.core.presentation.viewholder.BaseViewHolder
import com.f0x1d.logfox.feature.database.model.UserFilter
import com.f0x1d.logfox.feature.filters.presentation.databinding.ItemFilterBinding
import com.f0x1d.logfox.feature.strings.Strings

class FilterViewHolder(
    binding: ItemFilterBinding,
    click: (UserFilter) -> Unit,
    delete: (UserFilter) -> Unit,
    checked: (UserFilter, Boolean) -> Unit,
) : BaseViewHolder<UserFilter, ItemFilterBinding>(binding) {

    private val checkedListener =
        OnlyUserCheckedChangeListener(binding.enabledBox) { _, isChecked ->
            checked(currentItem ?: return@OnlyUserCheckedChangeListener, isChecked)
        }

    init {
        binding.apply {
            root.setOnClickListener {
                click(currentItem ?: return@setOnClickListener)
            }
            deleteButton.setOnClickListener {
                delete(currentItem ?: return@setOnClickListener)
            }
            enabledBox.setOnCheckedChangeListener(checkedListener)
        }
    }

    override fun ItemFilterBinding.bindTo(data: UserFilter) {
        includingText.setText(if (data.including) Strings.including else Strings.excluding)
        allowedLevelsText.setTextOrMakeGoneIfEmpty(
            Strings.log_levels,
            data.allowedLevels.joinToString {
                it.letter
            },
        )
        uidText.setTextOrMakeGoneIfEmpty(Strings.uid, data.uid)
        pidText.setTextOrMakeGoneIfEmpty(Strings.pid, data.pid)
        tidText.setTextOrMakeGoneIfEmpty(Strings.tid, data.tid)
        packageNameText.setTextOrMakeGoneIfEmpty(Strings.package_name, data.packageName)
        tagText.setTextOrMakeGoneIfEmpty(Strings.tag, data.tag)
        contentText.setTextOrMakeGoneIfEmpty(Strings.content_contains, data.content)

        checkedListener.check(data.enabled)
    }

    private fun TextView.setTextOrMakeGoneIfEmpty(prefix: Int, content: String?) {
        visibility = if (content.isNullOrEmpty()) View.GONE else View.VISIBLE

        if (content != null) {
            text = Html.fromHtml("<b>${context.getString(prefix)}:</b> $content")
        }
    }
}

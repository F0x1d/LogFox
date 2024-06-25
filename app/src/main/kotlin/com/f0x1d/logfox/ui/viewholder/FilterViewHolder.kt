package com.f0x1d.logfox.ui.viewholder

import android.text.Html
import android.view.View
import android.widget.TextView
import com.f0x1d.logfox.R
import com.f0x1d.logfox.arch.ui.viewholder.BaseViewHolder
import com.f0x1d.logfox.database.entity.UserFilter
import com.f0x1d.logfox.databinding.ItemFilterBinding
import com.f0x1d.logfox.utils.view.OnlyUserCheckedChangeListener

class FilterViewHolder(
    binding: ItemFilterBinding,
    click: (UserFilter) -> Unit,
    delete: (UserFilter) -> Unit,
    checked: (UserFilter, Boolean) -> Unit
): BaseViewHolder<UserFilter, ItemFilterBinding>(binding) {

    private val checkedListener = OnlyUserCheckedChangeListener(binding.enabledBox) { _, isChecked ->
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
        includingText.setText(if (data.including) R.string.including else R.string.excluding)
        allowedLevelsText.setTextOrMakeGoneIfEmpty(R.string.log_levels, data.allowedLevels.joinToString { it.letter })
        uidText.setTextOrMakeGoneIfEmpty(R.string.uid, data.uid)
        pidText.setTextOrMakeGoneIfEmpty(R.string.pid, data.pid)
        tidText.setTextOrMakeGoneIfEmpty(R.string.tid, data.tid)
        packageNameText.setTextOrMakeGoneIfEmpty(R.string.package_name, data.packageName)
        tagText.setTextOrMakeGoneIfEmpty(R.string.tag, data.tag)
        contentText.setTextOrMakeGoneIfEmpty(R.string.content_contains, data.content)

        checkedListener.check(data.enabled)
    }

    private fun TextView.setTextOrMakeGoneIfEmpty(prefix: Int, content: String?) {
        visibility = if (content.isNullOrEmpty()) View.GONE else View.VISIBLE

        if (content != null) {
            text = Html.fromHtml("<b>${context.getString(prefix)}:</b> $content")
        }
    }
}

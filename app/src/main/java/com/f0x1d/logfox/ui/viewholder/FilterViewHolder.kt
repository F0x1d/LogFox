package com.f0x1d.logfox.ui.viewholder

import android.text.Html
import android.view.View
import android.widget.TextView
import com.f0x1d.logfox.R
import com.f0x1d.logfox.database.entity.UserFilter
import com.f0x1d.logfox.databinding.ItemFilterBinding
import com.f0x1d.logfox.ui.viewholder.base.BaseViewHolder
import com.f0x1d.logfox.utils.view.OnlyUserCheckedChangeListener

class FilterViewHolder(
    binding: ItemFilterBinding,
    click: (UserFilter) -> Unit,
    delete: (UserFilter) -> Unit,
    checked: (UserFilter, Boolean) -> Unit
): BaseViewHolder<UserFilter, ItemFilterBinding>(binding) {

    private val checkedListener = OnlyUserCheckedChangeListener(binding.enabledBox) { button, isChecked ->
        checked(currentItem ?: return@OnlyUserCheckedChangeListener, isChecked)
    }

    init {
        binding.root.setOnClickListener {
            click(currentItem ?: return@setOnClickListener)
        }
        binding.deleteButton.setOnClickListener {
            delete(currentItem ?: return@setOnClickListener)
        }
        binding.enabledBox.setOnCheckedChangeListener(checkedListener)
    }

    override fun bindTo(data: UserFilter) {
        binding.includingText.setText(if (data.including) R.string.including else R.string.excluding)
        binding.allowedLevelsText.setTextOrMakeGoneIfEmpty(R.string.log_levels, data.allowedLevels.joinToString { it.letter })
        binding.uidText.setTextOrMakeGoneIfEmpty(R.string.uid, data.uid)
        binding.pidText.setTextOrMakeGoneIfEmpty(R.string.pid, data.pid)
        binding.tidText.setTextOrMakeGoneIfEmpty(R.string.tid, data.tid)
        binding.packageNameText.setTextOrMakeGoneIfEmpty(R.string.package_name, data.packageName)
        binding.tagText.setTextOrMakeGoneIfEmpty(R.string.tag, data.tag)
        binding.contentText.setTextOrMakeGoneIfEmpty(R.string.content_contains, data.content)

        checkedListener.check(data.enabled)
    }

    private fun TextView.setTextOrMakeGoneIfEmpty(prefix: Int, content: String?) {
        visibility = if (content.isNullOrEmpty()) View.GONE else View.VISIBLE

        if (content != null) {
            text = Html.fromHtml("<b>${context.getString(prefix)}:</b> $content")
        }
    }
}
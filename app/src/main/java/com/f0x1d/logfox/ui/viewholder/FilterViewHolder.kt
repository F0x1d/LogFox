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
        checked.invoke(currentItem, isChecked)
    }

    init {
        binding.root.setOnClickListener {
            click.invoke(currentItem)
        }
        binding.deleteButton.setOnClickListener {
            delete.invoke(currentItem)
        }
        binding.enabledBox.setOnCheckedChangeListener(checkedListener)
    }

    override fun bindTo(data: UserFilter) {
        binding.includingText.setText(if (data.including) R.string.including else R.string.excluding)
        binding.allowedLevelsText.setTextOrMakeGoneIfNull(R.string.log_levels, data.allowedLevels.joinToString { it.letter })
        binding.pidText.setTextOrMakeGoneIfNull(R.string.pid, data.pid)
        binding.tidText.setTextOrMakeGoneIfNull(R.string.tid, data.tid)
        binding.tagText.setTextOrMakeGoneIfNull(R.string.tag, data.tag)
        binding.contentText.setTextOrMakeGoneIfNull(R.string.content_contains, data.content)

        checkedListener.check(data.enabled)
    }

    private fun TextView.setTextOrMakeGoneIfNull(prefix: Int, content: String?) {
        visibility = if (content == null) View.GONE else View.VISIBLE

        if (content != null) {
            text = Html.fromHtml("<b>${context.getString(prefix)}:</b> $content")
        }
    }
}
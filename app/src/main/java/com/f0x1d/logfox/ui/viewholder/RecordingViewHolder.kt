package com.f0x1d.logfox.ui.viewholder

import com.f0x1d.logfox.database.entity.LogRecording
import com.f0x1d.logfox.databinding.ItemRecordingBinding
import com.f0x1d.logfox.extensions.toLocaleString
import com.f0x1d.logfox.ui.viewholder.base.BaseViewHolder

class RecordingViewHolder(
    binding: ItemRecordingBinding,
    click: (LogRecording) -> Unit,
    delete: (LogRecording) -> Unit
): BaseViewHolder<LogRecording, ItemRecordingBinding>(binding) {

    init {
        binding.root.setOnClickListener {
            click(currentItem ?: return@setOnClickListener)
        }
        binding.deleteButton.setOnClickListener {
            delete(currentItem ?: return@setOnClickListener)
        }
    }

    override fun bindTo(data: LogRecording) {
        binding.title.text = data.title
        binding.dateText.text = data.dateAndTime.toLocaleString()
    }
}
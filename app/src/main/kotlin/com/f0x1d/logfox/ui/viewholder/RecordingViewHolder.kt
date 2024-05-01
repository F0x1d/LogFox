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
        binding.apply {
            root.setOnClickListener {
                click(currentItem ?: return@setOnClickListener)
            }
            deleteButton.setOnClickListener {
                delete(currentItem ?: return@setOnClickListener)
            }
        }
    }

    override fun ItemRecordingBinding.bindTo(data: LogRecording) {
        title.text = data.title
        dateText.text = data.dateAndTime.toLocaleString()
    }
}
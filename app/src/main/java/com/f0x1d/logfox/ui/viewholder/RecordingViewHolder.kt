package com.f0x1d.logfox.ui.viewholder

import com.f0x1d.logfox.database.LogRecording
import com.f0x1d.logfox.databinding.ItemRecordingBinding
import com.f0x1d.logfox.extensions.toLocaleString
import com.f0x1d.logfox.ui.viewholder.base.BaseViewHolder

class RecordingViewHolder(binding: ItemRecordingBinding,
                          click: (LogRecording) -> Unit,
                          delete: (LogRecording) -> Unit): BaseViewHolder<LogRecording, ItemRecordingBinding>(binding) {

    init {
        binding.root.setOnClickListener {
            click.invoke(currentItem)
        }
        binding.deleteButton.setOnClickListener {
            delete.invoke(currentItem)
        }
    }

    override fun bindTo(data: LogRecording) {
        binding.recordingText.text = data.dateAndTime.toLocaleString()
    }
}
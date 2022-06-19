package com.f0x1d.logfox.ui.viewholder

import com.f0x1d.logfox.database.LogRecording
import com.f0x1d.logfox.databinding.ItemRecordingBinding
import com.f0x1d.logfox.extensions.toLocaleString
import com.f0x1d.logfox.ui.viewholder.base.BaseViewHolder

class RecordingViewHolder(binding: ItemRecordingBinding, private val block: (LogRecording) -> Unit): BaseViewHolder<LogRecording, ItemRecordingBinding>(binding) {

    init {
        itemView.setOnClickListener {
            block.invoke(currentItem)
        }
    }

    override fun bindTo(data: LogRecording) {
        binding.recordingText.text = data.dateAndTime.toLocaleString()
    }
}
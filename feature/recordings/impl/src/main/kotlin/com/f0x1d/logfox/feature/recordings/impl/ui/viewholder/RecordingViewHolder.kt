package com.f0x1d.logfox.feature.recordings.impl.ui.viewholder

import com.f0x1d.logfox.arch.ui.viewholder.BaseViewHolder
import com.f0x1d.logfox.database.entity.LogRecording
import com.f0x1d.logfox.feature.recordings.impl.databinding.ItemRecordingBinding
import java.util.Date

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
        dateText.text = Date(data.dateAndTime).toLocaleString()
    }
}

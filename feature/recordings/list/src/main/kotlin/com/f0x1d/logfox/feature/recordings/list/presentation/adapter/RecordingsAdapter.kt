package com.f0x1d.logfox.feature.recordings.list.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.f0x1d.logfox.arch.presentation.adapter.BaseListAdapter
import com.f0x1d.logfox.database.entity.LogRecording
import com.f0x1d.logfox.feature.recordings.list.databinding.ItemRecordingBinding
import com.f0x1d.logfox.feature.recordings.list.presentation.ui.viewholder.RecordingViewHolder
import com.f0x1d.logfox.model.diffCallback

class RecordingsAdapter(
    private val click: (LogRecording) -> Unit,
    private val delete: (LogRecording) -> Unit
): BaseListAdapter<LogRecording, ItemRecordingBinding>(diffCallback<LogRecording>()) {

    override fun createHolder(layoutInflater: LayoutInflater, parent: ViewGroup) =
        RecordingViewHolder(
            binding = ItemRecordingBinding.inflate(layoutInflater, parent, false),
            click = click,
            delete = delete,
        )
}

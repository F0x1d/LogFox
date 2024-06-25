package com.f0x1d.logfox.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import com.f0x1d.logfox.arch.adapter.BaseListAdapter
import com.f0x1d.logfox.database.entity.LogRecording
import com.f0x1d.logfox.databinding.ItemRecordingBinding
import com.f0x1d.logfox.ui.viewholder.RecordingViewHolder

class RecordingsAdapter(
    private val click: (LogRecording) -> Unit,
    private val delete: (LogRecording) -> Unit
): BaseListAdapter<LogRecording, ItemRecordingBinding>(RECORDING_DIFF) {

    companion object {
        private val RECORDING_DIFF = object : DiffUtil.ItemCallback<LogRecording>() {
            override fun areItemsTheSame(oldItem: LogRecording, newItem: LogRecording) = oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: LogRecording, newItem: LogRecording) = oldItem == newItem
        }
    }

    override fun createHolder(layoutInflater: LayoutInflater, parent: ViewGroup) = RecordingViewHolder(
        binding = ItemRecordingBinding.inflate(layoutInflater, parent, false),
        click = click,
        delete = delete
    )
}

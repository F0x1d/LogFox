package com.f0x1d.logfox.model

import android.annotation.SuppressLint
import androidx.recyclerview.widget.DiffUtil
import com.f0x1d.logfox.model.logline.LogLine

interface Identifiable {
    val id: Any
}

inline fun <reified T : Identifiable> diffCallback() = object : DiffUtil.ItemCallback<T>() {
    override fun areItemsTheSame(oldItem: T, newItem: T) = oldItem.id == newItem.id

    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(oldItem: T, newItem: T) = oldItem == newItem
}

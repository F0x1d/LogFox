package com.f0x1d.logfox.core.recycler

import android.annotation.SuppressLint
import androidx.recyclerview.widget.DiffUtil

interface Identifiable {
    val id: Any
}

inline fun <reified T : Identifiable> diffCallback() = object : DiffUtil.ItemCallback<T>() {
    override fun areItemsTheSame(oldItem: T, newItem: T) = oldItem.id == newItem.id

    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(oldItem: T, newItem: T) = oldItem == newItem
}

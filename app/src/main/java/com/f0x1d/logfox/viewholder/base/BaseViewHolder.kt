package com.f0x1d.logfox.viewholder.base

import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.f0x1d.logfox.adapter.base.BaseAdapter

abstract class BaseViewHolder<T, D : ViewBinding>(protected val binding: D): RecyclerView.ViewHolder(binding.root) {

    val baseAdapter
        get() = bindingAdapter as BaseAdapter<T, D>
    val elements
        get() = baseAdapter.elements

    abstract fun bindTo(data: T)
    open fun recycle() {}

    fun <R> adapter() = bindingAdapter as R
}
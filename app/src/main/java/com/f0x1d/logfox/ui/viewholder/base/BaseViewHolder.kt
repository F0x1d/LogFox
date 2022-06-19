package com.f0x1d.logfox.ui.viewholder.base

import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.f0x1d.logfox.adapter.base.BaseAdapter

abstract class BaseViewHolder<T, D : ViewBinding>(protected val binding: D): RecyclerView.ViewHolder(binding.root) {

    protected val baseAdapter
        get() = bindingAdapter as BaseAdapter<T, D>
    protected val elements
        get() = baseAdapter.elements
    protected val currentItem: T
        get() = elements[bindingAdapterPosition]

    abstract fun bindTo(data: T)
    open fun recycle() {}

    fun <R> adapter() = bindingAdapter as R
}
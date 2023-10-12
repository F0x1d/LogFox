package com.f0x1d.logfox.ui.viewholder.base

import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.f0x1d.logfox.adapter.base.BaseListAdapter

abstract class BaseViewHolder<T, D : ViewBinding>(protected val binding: D): RecyclerView.ViewHolder(binding.root) {

    protected val baseAdapter get() = bindingAdapter as BaseListAdapter<T, D>
    protected val elements: List<T> get() = baseAdapter.currentList
    protected val currentItem: T? get() = elements.getOrNull(bindingAdapterPosition)

    abstract fun bindTo(data: T)
    open fun recycle() {}
    open fun detach() {}

    fun <R> adapter() = bindingAdapter as R
}
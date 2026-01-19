package com.f0x1d.logfox.core.presentation.viewholder

import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.f0x1d.logfox.core.presentation.adapter.BaseListAdapter

abstract class BaseViewHolder<T, D : ViewBinding>(
    protected val binding: D
): RecyclerView.ViewHolder(binding.root) {

    @Suppress("UNCHECKED_CAST")
    protected val baseAdapter get() = bindingAdapter as? BaseListAdapter<T, D>
    protected val elements: List<T> get() = baseAdapter?.currentList ?: emptyList()
    protected val currentItem: T? get() = elements.getOrNull(bindingAdapterPosition)

    protected abstract fun D.bindTo(data: T)
    protected open fun D.recycle() = Unit
    protected open fun D.detach() = Unit

    fun bindTo(data: T) = binding.bindTo(data)
    fun recycle() = binding.recycle()
    fun detach() = binding.detach()

    @Suppress("UNCHECKED_CAST")
    protected fun <R> adapter() = bindingAdapter as? R
}

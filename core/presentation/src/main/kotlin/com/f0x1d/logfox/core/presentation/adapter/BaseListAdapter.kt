package com.f0x1d.logfox.core.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.f0x1d.logfox.core.presentation.viewholder.BaseViewHolder

abstract class BaseListAdapter<T, D : ViewBinding>(
    diffUtil: DiffUtil.ItemCallback<T>,
): ListAdapter<T, BaseViewHolder<T, D>>(diffUtil) {

    protected var recyclerView: RecyclerView? = null

    abstract fun createHolder(layoutInflater: LayoutInflater, parent: ViewGroup): BaseViewHolder<T, D>

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = createHolder(
        layoutInflater = LayoutInflater.from(parent.context),
        parent = parent,
    )

    override fun onBindViewHolder(holder: BaseViewHolder<T, D>, position: Int) = holder.bindTo(getItem(position))

    override fun onViewRecycled(holder: BaseViewHolder<T, D>) {
        holder.recycle()
    }

    override fun onViewDetachedFromWindow(holder: BaseViewHolder<T, D>) {
        holder.detach()
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        this.recyclerView = recyclerView
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        this.recyclerView = null
    }
}

package com.f0x1d.logfox.adapter.base

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.f0x1d.logfox.ui.viewholder.base.BaseViewHolder

abstract class BaseAdapter<T, D : ViewBinding>: RecyclerView.Adapter<BaseViewHolder<T, D>>() {

    open val updateWhenSet = true

    var elements = emptyList<T>()
        set(value) {
            field = value
            if (updateWhenSet) notifyDataSetChanged()
        }

    protected var recyclerView: RecyclerView? = null

    abstract fun createHolder(layoutInflater: LayoutInflater, parent: ViewGroup): BaseViewHolder<T, D>

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = createHolder(
        LayoutInflater.from(parent.context),
        parent
    )

    override fun onBindViewHolder(holder: BaseViewHolder<T, D>, position: Int) = holder.bindTo(elements[position])

    override fun getItemCount() = elements.size

    override fun onViewRecycled(holder: BaseViewHolder<T, D>) {
        super.onViewRecycled(holder)
        holder.recycle()
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
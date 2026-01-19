package com.f0x1d.logfox.core.ui.glide.icon

import android.content.Context
import android.graphics.drawable.Drawable
import com.bumptech.glide.Priority
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.data.DataFetcher

class IconDataFetcher(private val context: Context, private val packageName: String) : DataFetcher<Drawable> {

    override fun loadData(priority: Priority, callback: DataFetcher.DataCallback<in Drawable>) {
        try {
            callback.onDataReady(context.packageManager.getApplicationIcon(packageName))
        } catch (e: Exception) {
            callback.onLoadFailed(e)
        }
    }

    override fun cleanup() = Unit

    override fun cancel() = Unit

    override fun getDataClass() = Drawable::class.java

    override fun getDataSource() = DataSource.LOCAL
}

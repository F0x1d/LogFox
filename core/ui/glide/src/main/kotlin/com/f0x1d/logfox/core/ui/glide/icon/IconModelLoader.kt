package com.f0x1d.logfox.core.ui.glide.icon

import android.content.Context
import android.graphics.drawable.Drawable
import com.bumptech.glide.load.Options
import com.bumptech.glide.load.model.ModelLoader
import com.bumptech.glide.signature.ObjectKey

class IconModelLoader(private val context: Context) : ModelLoader<String, Drawable> {

    override fun buildLoadData(model: String, width: Int, height: Int, options: Options) = ModelLoader.LoadData(
        ObjectKey(model),
        IconDataFetcher(
            context,
            model.replace("icon:", ""),
        ),
    )

    override fun handles(model: String) = model.startsWith("icon:")
}

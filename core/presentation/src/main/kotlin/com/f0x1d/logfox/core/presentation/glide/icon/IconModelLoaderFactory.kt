package com.f0x1d.logfox.core.presentation.glide.icon

import android.content.Context
import android.graphics.drawable.Drawable
import com.bumptech.glide.load.model.ModelLoaderFactory
import com.bumptech.glide.load.model.MultiModelLoaderFactory

class IconModelLoaderFactory(
    private val context: Context
): ModelLoaderFactory<String, Drawable> {

    override fun build(multiFactory: MultiModelLoaderFactory) = IconModelLoader(context)

    override fun teardown() = Unit
}

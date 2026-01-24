package com.f0x1d.logfox.core.ui.glide

import android.widget.ImageView
import com.bumptech.glide.Glide
import com.f0x1d.logfox.core.ui.icons.Icons

fun ImageView.loadIcon(pkg: String) {
    Glide
        .with(this)
        .load("icon:$pkg")
        .error(Icons.ic_bug)
        .into(this)
}

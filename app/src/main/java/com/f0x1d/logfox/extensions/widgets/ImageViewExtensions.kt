package com.f0x1d.logfox.extensions.widgets

import android.widget.ImageView
import com.bumptech.glide.Glide
import com.f0x1d.logfox.R

fun ImageView.loadIcon(pkg: String) {
    Glide
        .with(this)
        .load("icon:${pkg}")
        .error(R.drawable.ic_bug)
        .into(this)
}
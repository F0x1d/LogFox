package com.f0x1d.logfox.ui.view

import android.widget.ImageView
import com.bumptech.glide.Glide
import com.f0x1d.logfox.ui.R

fun ImageView.loadIcon(pkg: String) {
    Glide
        .with(this)
        .load("icon:${pkg}")
        .error(R.drawable.ic_bug)
        .into(this)
}

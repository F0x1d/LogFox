package com.f0x1d.logfox.extensions

import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.Insets
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams

fun View.applyTopInsets(view: View) = applyInsets(view) { insets ->
    updateLayoutParams<ViewGroup.MarginLayoutParams> {
        topMargin = insets.top
    }
}

fun View.applyBottomInsets(view: View) = applyInsets(view) { insets ->
    updateLayoutParams<ViewGroup.MarginLayoutParams> {
        bottomMargin = insets.bottom
    }
}

fun View.applyInsets(view: View, block: View.(Insets) -> Unit) {
    ViewCompat.setOnApplyWindowInsetsListener(view) { _, windowInsets ->
        val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())

        block.invoke(this, insets)

        windowInsets
    }
}
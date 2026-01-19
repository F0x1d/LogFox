package com.f0x1d.logfox.core.presentation.view

import androidx.annotation.StringRes
import androidx.appcompat.widget.TooltipCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton

fun FloatingActionButton.setDescription(@StringRes resId: Int) = setDescription(context.getString(resId))

fun FloatingActionButton.setDescription(text: String) {
    contentDescription = text
    TooltipCompat.setTooltipText(this, text)
}

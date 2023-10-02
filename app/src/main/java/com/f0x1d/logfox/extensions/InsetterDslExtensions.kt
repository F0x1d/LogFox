package com.f0x1d.logfox.extensions

import android.content.Context
import dev.chrisbanes.insetter.InsetterApplyTypeDsl


fun InsetterApplyTypeDsl.paddingRelative(
    context: Context,
    start: Boolean = false,
    top: Boolean = false,
    end: Boolean = false,
    bottom: Boolean = false,
    horizontal: Boolean = false,
    vertical: Boolean = false,
    animated: Boolean = false,
) {
    padding(
        left = if (context.isRtl) end else start,
        top = top,
        right = if (context.isRtl) start else end,
        bottom = bottom,
        horizontal = horizontal,
        vertical = vertical,
        animated = animated
    )
}

fun InsetterApplyTypeDsl.marginRelative(
    context: Context,
    start: Boolean = false,
    top: Boolean = false,
    end: Boolean = false,
    bottom: Boolean = false,
    horizontal: Boolean = false,
    vertical: Boolean = false,
    animated: Boolean = false,
) {
    margin(
        left = if (context.isRtl) end else start,
        top = top,
        right = if (context.isRtl) start else end,
        bottom = bottom,
        horizontal = horizontal,
        vertical = vertical,
        animated = animated
    )
}
package com.f0x1d.logfox.core.ui.view

import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import androidx.core.widget.NestedScrollView

class CustomNestedScrollView(context: Context, attributeSet: AttributeSet) : NestedScrollView(context, attributeSet) {
    // https://github.com/F0x1d/LogFox/issues/107
    override fun computeScrollDeltaToGetChildRectOnScreen(rect: Rect?) = 0
}

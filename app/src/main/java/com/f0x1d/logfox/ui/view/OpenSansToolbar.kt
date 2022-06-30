package com.f0x1d.logfox.ui.view

import android.content.Context
import android.util.AttributeSet
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.children
import com.f0x1d.logfox.R
import com.google.android.material.appbar.MaterialToolbar

class OpenSansToolbar(context: Context, attributeSet: AttributeSet) : MaterialToolbar(context, attributeSet) {

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)

        children.forEach {
            if (it is TextView) it.typeface = ResourcesCompat.getFont(context, R.font.google_sans_medium)
        }
    }
}
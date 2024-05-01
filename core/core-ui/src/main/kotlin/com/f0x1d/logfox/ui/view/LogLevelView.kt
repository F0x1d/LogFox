package com.f0x1d.logfox.ui.view

import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import com.f0x1d.logfox.model.LogLevel
import com.f0x1d.logfox.ui.loglevel.backgroundColorId
import com.f0x1d.logfox.ui.loglevel.foregroundColorId
import com.google.android.material.textview.MaterialTextView

class LogLevelView(
    context: Context,
    attributeSet: AttributeSet
): MaterialTextView(context, attributeSet) {

    var logLevel: LogLevel? = null
        set(value) {
            field = value
            updateView()
        }

    private fun updateView() = logLevel?.let {
        text = it.letter

        val (backgroundColor, foregroundColor) = it.let {
            context.getColor(it.backgroundColorId) to context.getColor(it.foregroundColorId)
        }

        backgroundTintList = ColorStateList.valueOf(backgroundColor)
        setTextColor(foregroundColor)
    }
}

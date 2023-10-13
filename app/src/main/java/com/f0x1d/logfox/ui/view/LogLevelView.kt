package com.f0x1d.logfox.ui.view

import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import com.f0x1d.logfox.model.LogLevel
import com.google.android.material.textview.MaterialTextView
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class LogLevelView(
    context: Context,
    attributeSet: AttributeSet
): MaterialTextView(context, attributeSet) {

    @Inject
    lateinit var logLevelsColorsMappings: Map<LogLevel, Pair<Int, Int>>

    var logLevel: LogLevel? = null
        set(value) {
            field = value
            updateView()
        }

    private fun updateView() = logLevel?.let {
        text = it.letter

        val (backgroundColor, foregroundColor) = logLevelsColorsMappings[it] ?: return@let

        backgroundTintList = ColorStateList.valueOf(backgroundColor)
        setTextColor(foregroundColor)
    }
}
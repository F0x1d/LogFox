package com.f0x1d.logfox.extensions

import android.graphics.Color
import android.view.Window
import androidx.core.view.WindowCompat
import com.f0x1d.logfox.R
import com.f0x1d.logfox.extensions.context.resolveBoolean

fun Window.applyNavigationBarTheme(isContrastEnforced: Boolean = true) {
    WindowCompat.getInsetsController(this, decorView).apply {
        val isLightTheme = context.resolveBoolean(androidx.appcompat.R.attr.isLightTheme, false)

        isAppearanceLightStatusBars = isLightTheme
        isAppearanceLightNavigationBars = isLightTheme
    }

    navigationBarColor = when {
        !contrastedNavBarAvailable -> context.getColor(
            com.f0x1d.logfox.ui.R.color.transparent_black
        )

        !gesturesAvailable && isContrastEnforced -> context.getColor(
            R.color.navbar_transparent_background
        )

        else -> Color.TRANSPARENT
    }
}

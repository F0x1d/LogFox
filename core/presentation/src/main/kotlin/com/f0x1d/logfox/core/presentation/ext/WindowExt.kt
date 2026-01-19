package com.f0x1d.logfox.core.presentation.ext

import android.content.Context
import android.graphics.Color
import android.util.TypedValue
import android.view.Window
import androidx.annotation.AttrRes
import androidx.core.view.WindowCompat
import com.f0x1d.logfox.core.compat.contrastedNavBarAvailable
import com.f0x1d.logfox.core.compat.gesturesAvailable
import com.f0x1d.logfox.core.presentation.R

fun Window.enableEdgeToEdge(isContrastEnforced: Boolean = true) {
    WindowCompat.setDecorFitsSystemWindows(this, false)

    WindowCompat.getInsetsController(this, decorView).apply {
        val isLightTheme = context.resolveBoolean(
            attributeResId = androidx.appcompat.R.attr.isLightTheme,
            defaultValue = false,
        )

        isAppearanceLightStatusBars = isLightTheme
        isAppearanceLightNavigationBars = isLightTheme
    }

    navigationBarColor = when {
        !contrastedNavBarAvailable -> context.getColor(
            R.color.transparent_black
        )

        !gesturesAvailable && isContrastEnforced -> context.getColor(
            R.color.navbar_transparent_background
        )

        else -> Color.TRANSPARENT
    }
}

private fun Context.resolveAttribute(@AttrRes attributeResId: Int) = TypedValue().let {
    when (theme.resolveAttribute(attributeResId, it, true)) {
        true -> it

        else -> null
    }
}

private fun Context.resolveBoolean(@AttrRes attributeResId: Int, defaultValue: Boolean = false) = resolveAttribute(
    attributeResId = attributeResId,
).let {
    when (it != null && it.type == TypedValue.TYPE_INT_BOOLEAN) {
        true -> it.data != 0

        else -> defaultValue
    }
}

package com.f0x1d.logfox.ui.view

import android.content.Context
import android.util.AttributeSet
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.navigationrail.NavigationRailView

class CustomApplyInsetsNavigationRailView: NavigationRailView {

    constructor(ctx: Context): super(ctx)

    constructor(ctx: Context, attrs: AttributeSet): super(ctx, attrs) {
        ViewCompat.setOnApplyWindowInsetsListener(this) { _, windowInsets ->

            // applying only top and bottom padding
            windowInsets.getInsets(WindowInsetsCompat.Type.systemBars()).also { insets ->
                setPadding(
                    0,
                    insets.top,
                    0,
                    insets.bottom
                )
            }

            return@setOnApplyWindowInsetsListener windowInsets
        }
    }
}
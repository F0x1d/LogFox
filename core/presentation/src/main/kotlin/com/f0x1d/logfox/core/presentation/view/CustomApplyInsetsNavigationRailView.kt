package com.f0x1d.logfox.core.presentation.view

import android.content.Context
import android.util.AttributeSet
import com.google.android.material.navigationrail.NavigationRailView
import dev.chrisbanes.insetter.applyInsetter

class CustomApplyInsetsNavigationRailView: NavigationRailView {

    constructor(ctx: Context): super(ctx)

    constructor(ctx: Context, attrs: AttributeSet): super(ctx, attrs) {
        applyInsetter {
            type(
                navigationBars = true,
                statusBars = true,
            ) {
                padding(
                    left = false,
                    top = true,
                    right = false,
                    bottom = true,
                )
            }
        }
    }
}

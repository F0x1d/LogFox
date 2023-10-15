package com.f0x1d.logfox.extensions.views

import android.view.View
import android.view.accessibility.AccessibilityNodeInfo

fun View.setAccessibilityDelegateForClassName(clazz: Class<out View>) {
    accessibilityDelegate = object: View.AccessibilityDelegate() {
        override fun onInitializeAccessibilityNodeInfo(host: View, info: AccessibilityNodeInfo) {
            super.onInitializeAccessibilityNodeInfo(host, info)
            info.className = clazz.name
        }
    }
}
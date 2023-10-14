package com.f0x1d.logfox.extensions.views

import android.view.View
import android.view.accessibility.AccessibilityNodeInfo
import com.google.android.material.snackbar.Snackbar

fun View.snackbar(text: String) = Snackbar.make(this, text, Snackbar.LENGTH_SHORT).show()

fun View.setAccessibilityDelegateForClassName(clazz: Class<out View>) {
    accessibilityDelegate = object: View.AccessibilityDelegate() {
        override fun onInitializeAccessibilityNodeInfo(host: View, info: AccessibilityNodeInfo) {
            super.onInitializeAccessibilityNodeInfo(host, info)
            info.className = clazz.name
        }
    }
}
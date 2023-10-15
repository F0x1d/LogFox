package com.f0x1d.logfox.extensions.views

import android.view.View
import android.view.accessibility.AccessibilityNodeInfo
import android.widget.Button

fun View.replaceAccessibilityDelegateClassNameWith(clazz: Class<out View>) {
    accessibilityDelegate = object: View.AccessibilityDelegate() {
        override fun onInitializeAccessibilityNodeInfo(host: View, info: AccessibilityNodeInfo) {
            super.onInitializeAccessibilityNodeInfo(host, info)
            info.className = clazz.name
        }
    }
}
fun View.replaceAccessibilityDelegateClassNameWithButton() = replaceAccessibilityDelegateClassNameWith(Button::class.java)
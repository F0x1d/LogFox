package com.f0x1d.logfox.extensions

import android.os.Build

val isHuawei: Boolean get() {
    val containsHuawei: (String) -> Boolean = { input ->
        input.contains("huawei", ignoreCase = true)
    }

    return containsHuawei(Build.MANUFACTURER) || containsHuawei(Build.MODEL)
}
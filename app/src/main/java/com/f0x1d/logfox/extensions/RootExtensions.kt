package com.f0x1d.logfox.extensions

val haveRoot get() = try {
    Runtime.getRuntime().exec("su -c exit").waitFor() == 0
} catch (e: Exception) {
    false
}

enum class RootState {
    YES, NO, UNKNOWN
}

val RootState.haveRoot get() = this == RootState.YES
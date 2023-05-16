package com.f0x1d.logfox.extensions

enum class RootState {
    YES, NO, UNKNOWN
}

val RootState.haveRoot get() = this == RootState.YES
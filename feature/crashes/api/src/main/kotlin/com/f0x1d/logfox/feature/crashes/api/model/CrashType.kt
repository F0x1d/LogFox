package com.f0x1d.logfox.feature.crashes.api.model

enum class CrashType(val readableName: String) {
    JAVA("Java"),
    JNI("JNI"),
    ANR("ANR"),
}

package com.f0x1d.logfox.feature.database.entity

enum class CrashType(val readableName: String) {
    JAVA("Java"),
    JNI("JNI"),
    ANR("ANR"),
}

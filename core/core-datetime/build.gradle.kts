plugins {
    id("logfox.android.core")
    id("logfox.android.hilt")
}

android.namespace = "com.f0x1d.logfox.datetime"

dependencies {
    implementation(project(":core:core-preferences"))
}

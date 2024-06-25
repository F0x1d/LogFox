plugins {
    id("logfox.android.library")
    id("logfox.android.hilt")
}

android.namespace = "com.f0x1d.logfox.datetime"

dependencies {
    implementation(project(":strings"))

    implementation(project(":core:core-preferences"))
}

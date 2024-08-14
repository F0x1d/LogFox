plugins {
    id("logfox.android.core")
    id("logfox.android.hilt")
}

android.namespace = "com.f0x1d.logfox.preferences"

dependencies {
    implementation(projects.core.coreDatabase)

    implementation(libs.flow.preferences)
}

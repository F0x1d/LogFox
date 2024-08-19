plugins {
    id("logfox.android.core")
    id("logfox.android.hilt")
}

android.namespace = "com.f0x1d.logfox.preferences"

dependencies {
    implementation(projects.core.database)

    implementation(libs.flow.preferences)
}

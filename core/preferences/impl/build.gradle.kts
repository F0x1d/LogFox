plugins {
    alias(libs.plugins.logfox.android.library)
}

android.namespace = "com.f0x1d.logfox.core.preferences.impl"

dependencies {
    api(projects.core.preferences.api)

    implementation(libs.flow.preferences)
}

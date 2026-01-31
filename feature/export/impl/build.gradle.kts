plugins {
    alias(libs.plugins.logfox.android.feature)
}

android.namespace = "com.f0x1d.logfox.feature.export.impl"

dependencies {
    api(projects.feature.export.api)

    implementation(projects.core.di)
    implementation(projects.core.io)
}

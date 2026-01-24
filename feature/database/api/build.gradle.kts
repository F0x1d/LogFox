plugins {
    alias(libs.plugins.logfox.android.library)
}

android.namespace = "com.f0x1d.logfox.feature.database.api"

dependencies {
    api(projects.feature.logging.api)

    implementation(libs.kotlinx.coroutines.core)
}

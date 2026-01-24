plugins {
    alias(libs.plugins.logfox.android.feature)
}

android {
    namespace = "com.f0x1d.logfox.feature.terminals.presentation"
    buildFeatures.aidl = true
}

dependencies {
    implementation(projects.feature.terminals.api)

    implementation(libs.kotlinx.coroutines.core)
}

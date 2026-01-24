plugins {
    alias(libs.plugins.logfox.android.library)
    alias(libs.plugins.logfox.android.hilt)
    alias(libs.plugins.logfox.android.compose)
}

android {
    namespace = "com.f0x1d.logfox.core.ui.compose"

    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    api(projects.core.ui.base)
    api(projects.core.ui.compose.designSystem)

    implementation(libs.bundles.androidx.compose)
}

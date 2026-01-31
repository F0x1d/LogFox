plugins {
    alias(libs.plugins.logfox.android.library)
}

android.namespace = "com.f0x1d.logfox.feature.navigation.api"

dependencies {
    implementation(libs.bundles.androidx.navigation)
}

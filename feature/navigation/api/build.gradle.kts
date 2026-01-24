plugins {
    alias(libs.plugins.logfox.android.library)
}

android.namespace = "com.f0x1d.logfox.navigation"

dependencies {
    implementation(libs.bundles.androidx.navigation)
}

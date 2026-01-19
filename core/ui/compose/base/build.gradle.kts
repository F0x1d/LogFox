plugins {
    alias(libs.plugins.logfox.android.library)
    alias(libs.plugins.logfox.android.compose)
}

android.namespace = "com.f0x1d.logfox.compose.base"

dependencies {
    implementation(libs.bundles.androidx)
}

plugins {
    alias(libs.plugins.logfox.android.library)
    alias(libs.plugins.logfox.android.compose)
}

android.namespace = "com.f0x1d.logfox.compose.designsystem"

dependencies {
    api(projects.core.ui.compose.base)

    implementation(projects.strings)

    implementation(libs.bundles.androidx)
    implementation(libs.timber)
}

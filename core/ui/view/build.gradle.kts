plugins {
    alias(libs.plugins.logfox.android.library)
}

android {
    namespace = "com.f0x1d.logfox.core.ui.view"
}

dependencies {
    api(projects.core.ui.icons)

    implementation(projects.strings)

    implementation(libs.bundles.androidx)
    implementation(libs.bundles.androidx.navigation)
    implementation(libs.material)

    implementation(libs.insetter)
}

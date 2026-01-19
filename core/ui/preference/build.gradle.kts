plugins {
    alias(libs.plugins.logfox.android.library)
}

android.namespace = "com.f0x1d.logfox.core.ui.preference"

dependencies {
    implementation(projects.core.ui.dialog)
    implementation(projects.strings)

    implementation(libs.bundles.androidx)
    implementation(libs.material)
}

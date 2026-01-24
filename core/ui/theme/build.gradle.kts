plugins {
    alias(libs.plugins.logfox.android.library)
    alias(libs.plugins.logfox.android.hilt)
}

android {
    namespace = "com.f0x1d.logfox.core.ui.theme"
}

dependencies {
    implementation(projects.core.ui.preference)

    implementation(libs.viewpump)
}

plugins {
    alias(libs.plugins.logfox.android.library)
}

android {
    namespace = "com.f0x1d.logfox.core.ui.dialog"

    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    api(projects.core.ui.icons)

    implementation(projects.strings)

    implementation(libs.bundles.androidx)
    implementation(libs.material)
}

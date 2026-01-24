plugins {
    alias(libs.plugins.logfox.android.library)
    alias(libs.plugins.logfox.android.hilt)
}

android {
    namespace = "com.f0x1d.logfox.core.ui.base"

    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    api(projects.core.compat)
    api(projects.core.ui.theme)

    implementation(libs.bundles.androidx)
    implementation(libs.material)

    implementation(libs.insetter)
    implementation(libs.viewpump)
}

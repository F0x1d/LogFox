plugins {
    alias(libs.plugins.logfox.android.library)
    alias(libs.plugins.logfox.android.hilt)
    alias(libs.plugins.logfox.android.compose)
}

android {
    namespace = "com.f0x1d.logfox.core.presentation"

    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    api(projects.core.tea.android)
    api(projects.core.compose.designSystem)
    api(projects.core.compat)
    api(projects.core.recycler)
    
    implementation(projects.strings)
    
    implementation(libs.bundles.androidx)
    implementation(libs.bundles.androidx.compose)
    implementation(libs.bundles.androidx.navigation)
    implementation(libs.material)
    implementation(libs.timber)

    implementation(libs.insetter)
    implementation(libs.viewpump)

    implementation(libs.glide)
    ksp(libs.glide.compiler)
}

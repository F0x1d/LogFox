plugins {
    alias(libs.plugins.logfox.android.feature)
}

android {
    namespace = "com.f0x1d.logfox.feature.preferences.presentation"
    buildFeatures {
        viewBinding = true
        buildConfig = true
    }
}

dependencies {
    implementation(projects.feature.preferences.api)
    implementation(projects.feature.logging.api)
    implementation(projects.feature.terminals.api)
    implementation(projects.core.presentation)
    implementation(projects.core.context)
    implementation(projects.core.compat)
    implementation(projects.core.logging)
    implementation(projects.core.tea.android)
    implementation(projects.feature.navigation.api)

    implementation(projects.strings)
    implementation(libs.bundles.androidx)
    implementation(libs.bundles.androidx.navigation)
    implementation(libs.material)
    implementation(libs.androidx.hilt.navigation.fragment)
    implementation(libs.insetter)
}

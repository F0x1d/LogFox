plugins {
    alias(libs.plugins.logfox.android.feature.compose)
}

android.namespace = "com.f0x1d.logfox.feature.apps.picker.presentation"

dependencies {
    implementation(projects.feature.appsPicker.api)

    implementation(projects.strings)
    implementation(projects.core.di)
    implementation(projects.core.presentation)
    implementation(projects.core.tea.android)
    implementation(projects.core.compose.designSystem)
    implementation(projects.core.compose.base)

    implementation(libs.bundles.androidx)
    implementation(libs.bundles.androidx.navigation)
    implementation(libs.androidx.hilt.navigation.fragment)
    implementation(libs.coil.compose)
}

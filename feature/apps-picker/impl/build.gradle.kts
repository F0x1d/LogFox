plugins {
    alias(libs.plugins.logfox.android.feature.compose)
}

android.namespace = "com.f0x1d.logfox.feature.apps.picker"

dependencies {
    implementation(projects.strings)
    implementation(projects.core.di)
    implementation(projects.core.presentation)
    implementation(projects.core.compose.designSystem)
    implementation(projects.core.compose.base)

    implementation(projects.feature.appsPicker.api)

    implementation(libs.bundles.androidx)
    implementation(libs.bundles.androidx.navigation)
    implementation(libs.androidx.hilt.navigation.fragment)
    implementation(libs.coil.compose)
}

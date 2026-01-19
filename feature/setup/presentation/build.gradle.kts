plugins {
    alias(libs.plugins.logfox.android.feature.compose)
}

android.namespace = "com.f0x1d.logfox.feature.setup.presentation"

dependencies {
    implementation(projects.feature.setup.api)

    implementation(projects.core.presentation)
    implementation(projects.core.context)
    implementation(projects.core.tea.android)
    implementation(projects.feature.terminals.api)
    implementation(projects.core.compose.designSystem)
    implementation(projects.strings)

    implementation(libs.bundles.androidx)
    implementation(libs.bundles.androidx.navigation)
    implementation(libs.material)

    implementation(libs.androidx.hilt.navigation.fragment)

    testImplementation(projects.core.tests.compose)
    testImplementation(projects.core.tests.screenshot)
}

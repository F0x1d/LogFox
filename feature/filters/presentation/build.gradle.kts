plugins {
    alias(libs.plugins.logfox.android.feature)
}

android {
    namespace = "com.f0x1d.logfox.feature.filters.presentation"
    buildFeatures.viewBinding = true
}

dependencies {
    implementation(projects.feature.filters.api)
    implementation(projects.feature.appsPicker.api)
    implementation(projects.feature.logging.api)
    implementation(projects.feature.database.api)
    implementation(projects.core.di)
    implementation(projects.core.tea.android)
    implementation(projects.core.ui.base)
    implementation(projects.core.ui.icons)
    implementation(projects.core.ui.view)
    implementation(projects.core.ui.dialog)
    implementation(projects.core.recycler)
    implementation(projects.feature.navigation.api)

    implementation(projects.strings)
    implementation(libs.bundles.androidx)
    implementation(libs.bundles.androidx.navigation)
    implementation(libs.material)
    implementation(libs.androidx.hilt.navigation.fragment)
    implementation(libs.insetter)
}

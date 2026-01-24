plugins {
    alias(libs.plugins.logfox.android.feature)
}

android {
    namespace = "com.f0x1d.logfox.feature.logging.presentation"
    buildFeatures.viewBinding = true
}

dependencies {
    implementation(projects.feature.logging.api)
    implementation(projects.feature.filters.api)
    implementation(projects.feature.recordings.api)
    implementation(projects.feature.database.api)
    implementation(projects.feature.preferences.api)
    implementation(projects.core.tea.android)
    implementation(projects.core.ui.base)
    implementation(projects.core.ui.icons)
    implementation(projects.core.ui.view)
    implementation(projects.core.recycler)
    implementation(projects.core.di)
    implementation(projects.core.context)
    implementation(projects.core.copy)
    implementation(projects.feature.datetime.impl)
    implementation(projects.feature.navigation.api)

    implementation(projects.strings)
    implementation(libs.androidx.documentfile)
    implementation(libs.bundles.androidx)
    implementation(libs.bundles.androidx.navigation)
    implementation(libs.material)
    implementation(libs.insetter)
}

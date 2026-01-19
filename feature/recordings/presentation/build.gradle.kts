plugins {
    alias(libs.plugins.logfox.android.feature.compose)
}

android {
    namespace = "com.f0x1d.logfox.feature.recordings.presentation"
    buildFeatures.viewBinding = true
}

dependencies {
    implementation(projects.feature.recordings.api)
    implementation(projects.feature.database.api)
    implementation(projects.feature.preferences.api)
    implementation(projects.core.presentation)
    implementation(projects.core.di)
    implementation(projects.core.io)
    implementation(projects.core.context)
    implementation(projects.feature.datetime.api)
    implementation(projects.feature.navigation.api)
    implementation(projects.core.compose.designSystem)
    implementation(projects.strings)
    implementation(libs.bundles.androidx)
    implementation(libs.bundles.androidx.navigation)
    implementation(libs.material)
}

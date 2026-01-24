plugins {
    alias(libs.plugins.logfox.android.feature)
    alias(libs.plugins.logfox.android.parcelize)
}

android {
    namespace = "com.f0x1d.logfox.feature.terminals.impl"
    buildFeatures.buildConfig = true
}

dependencies {
    api(projects.feature.terminals.api)
    implementation(projects.feature.terminals.presentation)
    implementation(projects.feature.preferences.api)
    implementation(projects.core.di)
    implementation(projects.strings)
    implementation(libs.bundles.androidx)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.timber)
    implementation(libs.libsu)
    implementation(libs.bundles.shizuku)
}

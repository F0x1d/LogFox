plugins {
    alias(libs.plugins.logfox.android.library)
}

android.namespace = "com.f0x1d.logfox.feature.preferences.api"

dependencies {
    implementation(projects.feature.logging.api)
    implementation(projects.feature.database.api)
    implementation(projects.feature.terminals.api)
    implementation(projects.strings)
    implementation(libs.bundles.androidx)
    implementation(libs.flow.preferences)
}

plugins {
    alias(libs.plugins.logfox.android.feature)
}

android.namespace = "com.f0x1d.logfox.feature.preferences.impl"

dependencies {
    api(projects.feature.preferences.api)
    implementation(projects.feature.database.api)
    implementation(projects.feature.logging.api)
    implementation(projects.feature.terminals.api)
    implementation(projects.strings)
    implementation(libs.bundles.androidx)
    implementation(libs.timber)
    implementation(libs.flow.preferences)
}

plugins {
    alias(libs.plugins.logfox.android.library)
}

android.namespace = "com.f0x1d.logfox.feature.preferences.api"

dependencies {
    api(projects.core.preferences.api)

    implementation(projects.feature.database.api)
    implementation(projects.feature.terminals.api)
    implementation(projects.strings)

    implementation(libs.bundles.androidx)
}

plugins {
    alias(libs.plugins.logfox.android.feature)
}

android.namespace = "com.f0x1d.logfox.feature.recordings.api"

dependencies {
    implementation(projects.feature.logging.api)
    implementation(projects.feature.database.api)
    implementation(projects.feature.datetime.api)
    implementation(projects.feature.preferences.api)
    implementation(projects.strings)

    implementation(libs.bundles.androidx)
}

plugins {
    alias(libs.plugins.logfox.android.feature)
}

android.namespace = "com.f0x1d.logfox.feature.recordings.api"

dependencies {
    api(projects.core.recycler)

    implementation(projects.feature.logging.api)
    implementation(projects.feature.datetime.api)
    implementation(projects.feature.preferences.api)

    implementation(libs.bundles.androidx)
}

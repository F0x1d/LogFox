plugins {
    alias(libs.plugins.logfox.android.feature)
}

android.namespace = "com.f0x1d.logfox.feature.filters.api"

dependencies {
    implementation(projects.feature.database.api)
    implementation(projects.feature.logging.api)

    implementation(libs.bundles.androidx)
}

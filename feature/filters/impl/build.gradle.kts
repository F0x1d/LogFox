plugins {
    alias(libs.plugins.logfox.android.feature)
}

android.namespace = "com.f0x1d.logfox.feature.filters.impl"

dependencies {
    api(projects.feature.filters.api)

    implementation(projects.core.di)
    implementation(projects.feature.database.api)
    implementation(projects.feature.logging.api)

    implementation(libs.gson)
}

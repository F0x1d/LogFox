plugins {
    alias(libs.plugins.logfox.android.feature)
}

android.namespace = "com.f0x1d.logfox.feature.filters.impl"

dependencies {
    api(projects.feature.filters.api)

    implementation(projects.feature.export.api)
    implementation(projects.core.di)
    implementation(projects.feature.database.api)

    implementation(libs.gson)
}

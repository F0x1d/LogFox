plugins {
    id("logfox.android.feature")
}

android.namespace = "com.f0x1d.logfox.feature.filters.list"

dependencies {
    implementation(projects.feature.filters.api)

    implementation(libs.gson)
}

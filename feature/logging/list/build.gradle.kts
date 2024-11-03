plugins {
    id("logfox.android.feature")
}

android.namespace = "com.f0x1d.logfox.feature.logging.list"

dependencies {
    implementation(projects.feature.crashes.api)
    implementation(projects.feature.filters.api)
    implementation(projects.feature.logging.api)
    implementation(projects.feature.recordings.api)
}

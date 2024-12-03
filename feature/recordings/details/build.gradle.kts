plugins {
    id("logfox.android.feature")
}

android.namespace = "com.f0x1d.logfox.feature.recordings.details"

dependencies {
    implementation(projects.feature.recordings.api)
    implementation(projects.feature.logging.api)
}

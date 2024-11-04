plugins {
    id("logfox.android.feature.compose")
}

android.namespace = "com.f0x1d.logfox.feature.recordings.list"

dependencies {
    implementation(projects.feature.recordings.api)
    implementation(projects.feature.logging.api)
}
